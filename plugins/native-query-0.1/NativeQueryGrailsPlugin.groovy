import grails.util.*
import grails.orm.*
import grails.spring.*

import org.codehaus.groovy.grails.commons.*
import org.codehaus.groovy.grails.validation.*
import org.codehaus.groovy.grails.plugins.support.*
import org.codehaus.groovy.grails.orm.hibernate.*
import org.codehaus.groovy.grails.orm.hibernate.cfg.*
import org.codehaus.groovy.grails.orm.hibernate.support.*
import org.codehaus.groovy.grails.orm.hibernate.validation.*
import org.codehaus.groovy.grails.commons.spring.*
import org.codehaus.groovy.runtime.*
import org.codehaus.groovy.grails.commons.metaclass.*
import org.codehaus.groovy.grails.orm.hibernate.metaclass.*

import org.hibernate.*

import org.springframework.orm.hibernate3.*
import org.springframework.orm.hibernate3.support.*
import org.springmodules.beans.factory.config.*
import org.springframework.beans.*
import org.springframework.transaction.support.*
import org.springframework.context.*
import org.springframework.core.io.*
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.BeanDefinition

import jp.tskr.grails.plugin.hibernate.metaclass.ExecuteNativeQueryPersistentMethod;

class NativeQueryGrailsPlugin {
	def version = 0.1
	def dependsOn = [:]
	//def dependsOn = [dataSource: version,i18n: version,core: version]
	def loadAfter = ['controllers']

	def author = "Tatsuya Anno"
	def authorEmail = "taapps@gmail.com"
	def title = "Add new query method called executeNativeQuery"
	def description = '''\
test
'''

	// URL to the plugin's documentation
	def documentation = "http://grails.org/NativeQuery+Plugin"

	def doWithSpring = {
	}
	
	def doWithApplicationContext = { applicationContext ->
	}

	def doWithWebDescriptor = { xml ->
	}

	def doWithDynamicMethods = { ctx ->
		d("Calling custom doWithDynamicMethods")
		for (dc in application.domainClasses) {
			registerNewStaticMethod(dc,application,ctx)

			//for (subClass in dc.subClasses) {
			//	registerNewStaticMethod(subClass,application,ctx)
			//}
		}
	}

	private d={
		println("***DEBUG*** $it")
	}

	private registerNewStaticMethod(GrailsDomainClass dc, GrailsApplication application, ApplicationContext ctx)
	{
		d("Calling custom registerNewMethod")
		def metaClass = dc.metaClass
		SessionFactory sessionFactory = ctx.sessionFactory
		GroovyClassLoader classLoader = application.classLoader
		def Class domainClassType = dc.clazz

		def executeNativeQueryMethod = new ExecuteNativeQueryPersistentMethod(sessionFactory,classLoader)
		metaClass.'static'.executeNativeQuery = {String query ->
			executeNativeQueryMethod.invoke(domainClassType, "executeNativeQuery", [query] as Object[])
		}
		
		metaClass.'static'.executeNativeQuery = {String query,Map entityParams ->
			executeNativeQueryMethod.invoke(domainClassType, "executeNativeQuery", [query, entityParams] as Object[])
		}

		metaClass.'static'.executeNativeQuery = {String query,Map entityParams, Collection positionalParams ->
			executeNativeQueryMethod.invoke(domainClassType, "executeNativeQuery", [query, entityParams, positionalParams] as Object[])
		}

		metaClass.'static'.executeNativeQuery = {String query,Map entityParams, Map namedParams ->
			executeNativeQueryMethod.invoke(domainClassType, "executeNativeQuery", [query, entityParams, namedParams] as Object[])
		}

		metaClass.'static'.executeNativeQuery = {String query,Map entityParams, Collection positionalParams, Map paginateParams ->
			executeNativeQueryMethod.invoke(domainClassType, "executeNativeQuery", [query, entityParams, positionalParams, paginateParams] as Object[])
		}

		metaClass.'static'.executeNativeQuery = {String query,Map entityParams, Map namedParams, Map paginateParams ->
			executeNativeQueryMethod.invoke(domainClassType, "executeNativeQuery", [query, entityParams, namedParams, paginateParams] as Object[])
		}
	}

	def onChange = { event ->
	}

	def onConfigChange = { event ->
	}
}
