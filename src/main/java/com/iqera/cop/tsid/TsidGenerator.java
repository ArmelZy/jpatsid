package com.iqera.cop.tsid;

import java.lang.reflect.Member;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

public class TsidGenerator implements IdentifierGenerator {

    private final Tsid.Generator generator;

    public TsidGenerator(Tsid config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
        generator = config.generator();
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generator.random().toLong();
    }
}
