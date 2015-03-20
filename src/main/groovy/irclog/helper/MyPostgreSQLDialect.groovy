package irclog.helper

import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQLDialect
import org.hibernate.id.PersistentIdentifierGenerator
import org.hibernate.id.SequenceGenerator
import org.hibernate.type.Type

class MyPostgreSQLDialect extends PostgreSQLDialect {

    @Override
    Class<?> getNativeIdentifierGeneratorClass() {
        TableNameSequenceGenerator.class
    }

    static class TableNameSequenceGenerator extends SequenceGenerator {

        private static final SEQUENCE_SUFFIX = "seq"

        @Override
        void configure(Type type, Properties params, Dialect dialect) {
            if (!params.getProperty(SEQUENCE)) {
                String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE)
                if (tableName) {
                    params.setProperty(SEQUENCE, "${tableName}_id_${SEQUENCE_SUFFIX}")
                }
            }
            super.configure(type, params, dialect)
        }
    }
}
