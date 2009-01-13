/* Copyright 2004-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.tskr.grails.plugin.hibernate.metaclass;

import groovy.lang.MissingMethodException;
import groovy.lang.GString;
import org.codehaus.groovy.grails.orm.hibernate.exceptions.GrailsQueryException;
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil;
import org.hibernate.HibernateException;
//added by tatsuyaanno
//import org.hibernate.Query;
import org.hibernate.SQLQuery;
//end
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.codehaus.groovy.grails.orm.hibernate.metaclass.AbstractStaticPersistentMethod;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Allows the executing of native sql queries directly
 * This class was created based on ExecuteQueryPersistentMethod class
 *
 * <p/>
 * eg. Account.executeNativeQuery( "select distinct a.number from Account a where a.branch = ?", 'London' ) or
 * Account.executeNativeQuery( "select distinct a.number from Account a where a.branch = :branch", [branch:'London'] )
 *
 * @author Tatsuya Anno
 * @since 05-Mar-2008
 */
public class ExecuteNativeQueryPersistentMethod
        extends AbstractStaticPersistentMethod {

    private static final String METHOD_SIGNATURE = "executeNativeQuery";
    private static final Pattern METHOD_PATTERN = Pattern.compile( "^executeNativeQuery$" );
    private final boolean isFindQuery;

    public ExecuteNativeQueryPersistentMethod( SessionFactory sessionFactory, ClassLoader classLoader, boolean isFindQuery ) {
        super( sessionFactory, classLoader, METHOD_PATTERN );
        this.isFindQuery = isFindQuery;
    }

    protected Object doInvokeInternal( Class clazz, String methodName, Object[] arguments ) {
        checkMethodSignature( clazz, methodName, arguments );

        final Class finalizedClazz=clazz;
        final String query = arguments[0].toString();
        final Map entityParams = extractEntityParams( arguments,clazz );
        final Map paginateParams = extractPaginateParams( arguments );
        final List positionalParams = extractPositionalParams( arguments );
        final Map namedParams = extractNamedParams( arguments );

        return getHibernateTemplate().executeFind( new HibernateCallback() {
            public Object doInHibernate( Session session ) throws HibernateException, SQLException {
                SQLQuery q=session.createSQLQuery(query);
                for(Iterator iter=entityParams.entrySet().iterator();iter.hasNext();){
                        Map.Entry entry = (Map.Entry) iter.next();
                        if( !( entry.getKey() instanceof String ) )
                                throw new GrailsQueryException( "entity parameter's name must be of type String" );
                        String parameterName = (String) entry.getKey();
                        Object parameterValue = entry.getValue();
                        if(!(parameterValue instanceof Class)){
                                throw new GrailsQueryException( "entity parameter's name value must be of type Class" );
                        }
                        q.addEntity(parameterName,(Class)parameterValue);
                }

                // process paginate params
                if (isFindQuery) {
                    if( paginateParams.containsKey( GrailsHibernateUtil.ARGUMENT_MAX ) ) {
                        q.setMaxResults( ((Number)paginateParams.get( GrailsHibernateUtil.ARGUMENT_MAX ) ).intValue() );
                    }
                    if( paginateParams.containsKey( GrailsHibernateUtil.ARGUMENT_OFFSET ) ) {
                        q.setFirstResult( ((Number)paginateParams.remove( GrailsHibernateUtil.ARGUMENT_OFFSET )).intValue() );
                    }
                }
                // process positional SQL params
                int index = 0;
                for( Iterator iterator = positionalParams.iterator(); iterator.hasNext(); index++ ) {
                    Object parameter = iterator.next();
                    q.setParameter( index, parameter );
                }
                // process named SQL params
                for( Iterator iterator = namedParams.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    if( !( entry.getKey() instanceof String ) )
                        throw new GrailsQueryException( "Named parameter's name must be of type String" );
                    String parameterName = (String) entry.getKey();
                    Object parameterValue = entry.getValue();
                    if( Collection.class.isAssignableFrom( parameterValue.getClass() )) {
                        q.setParameterList( parameterName, (Collection) parameterValue );
                    } else if( parameterValue.getClass().isArray() ) {
                        q.setParameterList( parameterName, (Object[]) parameterValue );
                    } else if( parameterValue instanceof GString ) {
                        q.setParameter( parameterName, parameterValue.toString() );
                    } else {
                        q.setParameter( parameterName, parameterValue );
                    }
                }
                if (isFindQuery) {
                    return q.list();
                } else {
                    int count = q.executeUpdate();
                    return Arrays.asList(count); // 強引に第1要素として返す。
                }
            }
        } );
    }

/*
Parameter Matrix
Arg Number    :0       1    2     3        
Type 1        :String                  :Query
Type 2        :String  Map             :Query,EntityMap
Type 3        :String  Map  List       :Query,EntityMap,ParamList
Type 4        :String  Map  Map        :Query,EntityMap,NamedMap
Type 5        :String  Map  List  Map  :Query,EntityMap,ParamList,PageMap
Type 6        :String  Map  Map   Map  :Query,EntityMap,NamedMap,PageMap

[Enhancement]
Type 7        :String  List Map        :Query,ParamList,PageMap
Type 8        :String  Map  Map        :Query,NamedMap,PageMap
*/
    private void checkMethodSignature( Class clazz, String methodName, Object[] arguments ) {
        boolean valid = true;
        if( arguments.length < 1 ) valid = false;
        else if( arguments.length == 4 && !(arguments[3] instanceof Map) ) valid = false;
        else if( arguments.length > 4 ) valid = false;

        if( !valid ) throw new MissingMethodException( METHOD_SIGNATURE, clazz, arguments );
    }

    private Map extractEntityParams(Object[] arguments,Class defaultClazz){
       Map result = new HashMap();
       if(arguments.length<2 || !(arguments[1] instanceof Map)){
          result.put("tbl",defaultClazz);
       }
       else{
          result.putAll( (Map) arguments[1] );
       }
       return result;
    }

    private Map extractPaginateParams( Object[] arguments ) {
        Map result = new HashMap();
        int paginateParamsIndex = 0;
        if( arguments.length == 3 && arguments[2] instanceof Map ) paginateParamsIndex=2;
        else if( arguments.length == 4 ) paginateParamsIndex = 3;
        if( paginateParamsIndex > 0 ) {
            Map sourceMap = (Map) arguments[paginateParamsIndex];
            if( sourceMap.containsKey( GrailsHibernateUtil.ARGUMENT_MAX )) result.put( GrailsHibernateUtil.ARGUMENT_MAX, sourceMap.get(GrailsHibernateUtil.ARGUMENT_MAX));
            if( sourceMap.containsKey( GrailsHibernateUtil.ARGUMENT_OFFSET )) result.put( GrailsHibernateUtil.ARGUMENT_OFFSET, sourceMap.get(GrailsHibernateUtil.ARGUMENT_OFFSET));
        }
        return result;
    }

    private List extractPositionalParams( Object[] arguments ) {
        List result = new ArrayList();
        if( arguments.length < 3 || arguments[2] instanceof Map ) return result;
        else {
            if( arguments[2] instanceof Collection ) {
                result.addAll( (Collection) arguments[2] );
            } else if( arguments[2].getClass().isArray() ) {
                result.addAll( Arrays.asList( (Object[]) arguments[2] ) );
            } else {
                result.add( arguments[2] );
            }
        }
        return result;
    }

    private Map extractNamedParams( Object[] arguments ) {
        Map result = new HashMap();
        if( arguments.length < 3 || !(arguments[2] instanceof Map) ) return result;
        result.putAll( (Map) arguments[2] );
        // max and offset are processed by paginate params
        result.remove( GrailsHibernateUtil.ARGUMENT_MAX );
        result.remove( GrailsHibernateUtil.ARGUMENT_OFFSET );
        return result;
    }
}
