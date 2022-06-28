package com.tm.tdd.utils.validators;

import com.tm.tdd.service.IUserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintValidator;
import java.util.List;

public class TestConstraintValidationFactory extends SpringWebConstraintValidatorFactory{
    private final Log logger = LogFactory.getLog( getClass() );

    private final WebApplicationContext wac;

    private List<IUserService> services;

    public TestConstraintValidationFactory ( WebApplicationContext wac, List<IUserService> services )
    {
        this.wac = wac;
        this.services = services;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance ( Class<T> key )
    {
        logger.info( "key is : " + key );
        ConstraintValidator instance = super.getInstance( key );

        if ( instance instanceof UniqueEmailValidator )
        {
            UniqueEmailValidator ticketExistsValidator = ( UniqueEmailValidator )instance;
            ticketExistsValidator.setUserService( services.stream()
                    .filter( service -> service instanceof IUserService )
                    .map( IUserService.class::cast )
                    .findFirst().orElseThrow( () -> new IllegalArgumentException( "UserService not found in passed services list" ) ) );
            instance = ticketExistsValidator;
        }
        return ( T )instance;
    }

    @Override
    protected WebApplicationContext getWebApplicationContext ()
    {
        return wac;
    }
}
