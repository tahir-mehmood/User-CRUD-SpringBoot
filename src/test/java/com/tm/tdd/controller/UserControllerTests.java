package com.tm.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tm.tdd.CustomLocalValidatorFactoryBean;
import com.tm.tdd.controller.advices.UsersControllerAdvice;
import com.tm.tdd.domain.repository.UserRepository;
import com.tm.tdd.dto.UserDTO;
import com.tm.tdd.service.IUserService;
import com.tm.tdd.utils.validators.TestConstraintValidationFactory;
import com.tm.tdd.utils.validators.UniqueEmailValidator;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintValidator;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UsersController.class)
public class UserControllerTests {

    public static final String INVALID_EMAIL = "tahirgmail.com";
    public static final String BLANK_VALUE = "";
    public static final LocalDate INVALID_DOB = LocalDate.of(2023, 01, 01);

    public static final LocalDate VALID_DOB = LocalDate.of(1986, 01, 01);
    public static final String VALID_FIRST_NAME = "Tahir";
    public static final String VALID_LAST_NAME = "Mehmood";
    public static final String VALID_EMAIL = "tahir.nbsit@gmail.com";

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUserService userService;



    @InjectMocks
    UsersController usersController;

    @Autowired
    private MockServletContext servletContext;

    private final List<ConstraintValidator<?, ?>> customConstraintValidators =
            Arrays.asList(
                    new UniqueEmailValidator(userService));
    private final ValidatorFactory customValidatorFactory = new CustomLocalValidatorFactoryBean(customConstraintValidators);
    private final Validator validator = customValidatorFactory.getValidator();


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LocalValidatorFactoryBean validatorFactoryBean = getCustomValidatorFactoryBean();
        mockMvc = standaloneSetup(this.usersController)
                .setValidator(validatorFactoryBean)
                .setControllerAdvice(new UsersControllerAdvice())

                .build();
    }

    private LocalValidatorFactoryBean getCustomValidatorFactoryBean() {
        final GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        beanFactory.registerSingleton(UniqueEmailValidator.class.getCanonicalName(), new UniqueEmailValidator(userService));

        context.refresh();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setApplicationContext(context);

        List<IUserService> servicesUsedByValidators = new ArrayList<>();
        servicesUsedByValidators.add(this.userService);

        TestConstraintValidationFactory constraintFactory =
                new TestConstraintValidationFactory(context, servicesUsedByValidators);

        validatorFactoryBean.setConstraintValidatorFactory(constraintFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    @Test
    @DisplayName("In case there are no users, the Controller should return an empty response with response code 204.")
    public void testWhenNoUserExists_Success_GET() throws Exception {

        List<UserDTO> users = new ArrayList<>();
        given(userService.getAllUsers()).willThrow(new ResponseStatusException(HttpStatus.NO_CONTENT, "There are no users present in the system at the moment."));
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    @DisplayName("Should get Users when only 1 user is present. Size should be one. The resultant object should contain the expected values. Get Method returns Status success.")
    public void testWhenOnlyOneUserExists_Success_GET() throws Exception {

        List<UserDTO> users = new ArrayList<>();
        users.add(UserDTO.builder().id(1).firstName("Tahir").lastName("Mehmood").email("tahir.nbsit@gmail.com").dateOfBirth(LocalDate.of(1986, 07, 01)).build());

        given(userService.getAllUsers()).willReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Tahir"))
                .andExpect(jsonPath("$[0].lastName").value("Mehmood"))
                .andExpect(jsonPath("$[0].email").value("tahir.nbsit@gmail.com"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("01-07-1986"));
    }

    @Test
    @DisplayName("Creating new user with invalid first Name. Should Fail with BAD Request.")
    public void testWhen_CreateUSer_FirstNameBlank_Fail_POST() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(UserDTO.builder().firstName(BLANK_VALUE).build())))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Creating new user with invalid last Name. Should Fail with BAD Request.")
    public void testWhen_CreateUSer_LastNameBlank_Fail_POST() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(UserDTO.builder().lastName(BLANK_VALUE).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Creating new user with blank email. Should Fail with BAD Request.")
    public void testWhen_CreateUSer_EmailBlank_Fail_POST() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(UserDTO.builder().email(BLANK_VALUE).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Creating new user with Invalid email. Should Fail with BAD Request.")
    public void testWhen_CreateUSer_EmailInvalid_Fail_POST() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(UserDTO.builder().email(INVALID_EMAIL).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Creating new user with an already existent email. Should Fail with BAD Request.")
    public void testWhen_CreateUSer_EmailExisting_Fail_POST() throws Exception {
        given(userService.existsByEmail(VALID_EMAIL)).willReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(UserDTO.builder().firstName(VALID_FIRST_NAME).lastName(VALID_LAST_NAME).dateOfBirth(VALID_DOB).email(VALID_EMAIL).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Creating new user with an already existent email. Should Fail with BAD Request.")
    public void testWhen_CreateUSer_DOBInFuture_Fail_POST() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(UserDTO.builder().firstName(VALID_FIRST_NAME).lastName(VALID_LAST_NAME).dateOfBirth(INVALID_DOB).email(VALID_EMAIL).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Creating new user with valid information. Should pass with Status as CREATED")
    public void testWhen_CreateUSer_VALID_User_SUCCESS_POST() throws Exception {
       UserDTO user = UserDTO.builder().id(1).firstName(VALID_FIRST_NAME).lastName(VALID_LAST_NAME).dateOfBirth(VALID_DOB).email(VALID_EMAIL).build();
        given(userService.saveUser(isA(UserDTO.class))).willReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isCreated());

    }

}
