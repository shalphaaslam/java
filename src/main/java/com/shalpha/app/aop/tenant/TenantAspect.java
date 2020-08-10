package com.shalpha.app.aop.tenant;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shalpha.app.domain.User;
import com.shalpha.app.repository.UserRepository;
import com.shalpha.app.security.SecurityUtils;

@Aspect
@Component
public class TenantAspect {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
//
//	@Autowired
//	private TenantRepository tenantRepo;

	private final String fieldName = "tenantId";

	private final Logger log = LoggerFactory.getLogger(TenantAspect.class);

	/**
	 * Run method if User service is hit. Filter users based on which tenants the
	 * user is associated with. Skip filter if user has no tenants. For now, filter
	 * is not used but user tenant conditions are checked.
	 */
//	@Before("execution(* com.shalpha.app.service.UserService.*(..))")
	public void beforeExecution() throws Throwable {
		Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent()) {
			Optional<User> user = userRepository.findOneByLogin(login.get());
			if(user.isPresent()) {
				if (user.get().getTenant() != null) {
					try {
					Filter filter = entityManager.unwrap(Session.class).enableFilter("TENANTS_FILTER");
					filter.setParameter(fieldName, user.get().getTenant());
					} catch(Exception ex) {
						log.debug(ex.getMessage());
					}
				}
			}
		}
	}
	

//	@Before("execution(* com.shalpha.app.service.impl.KeycloakOauthImpl.*(..))")
//	public void beforeExecutionInternalKeycloakOperation() throws Throwable {
//		Optional<String> login = SecurityUtils.getCurrentUserLogin();
//		if (login.isPresent()) {
//			if (TenantContextUtil.getCurrentTenantUri() != null) {
//				URI uri = new URI((TenantContextUtil.getCurrentTenantUri().trim()).toLowerCase());
//				String path = uri.getPath();
//				String realm = path.substring(path.lastIndexOf('/') + 1);
//				ProfileConfiguration.profileProperties().getOauth().setRealm(realm);
//			} else {
//				// Open API hit without Auth header. New user registration
//			}
//		} else {
//			// throw new NoSuchElementException("No user identified!");
//			log.debug("User not identified..Assuming test user..");
//		}
//	}
//	
//	@Before("execution(* com.shalpha.app.service.impl.ResPermServiceImpl.*(..))")
//	public void beforeExecutionInternalAssetOperation() throws Throwable {
//		Optional<String> login = SecurityUtils.getCurrentUserLogin();
//		if (login.isPresent()) {
//			if (TenantContextUtil.getCurrentTenantUri() != null) {
//				URI uri = new URI((TenantContextUtil.getCurrentTenantUri().trim()).toLowerCase());
//				String path = uri.getPath();
//				String realm = path.substring(path.lastIndexOf('/') + 1);
//				ProfileConfiguration.profileProperties().getOauth().setRealm(realm);
//			} else {
//				// Open API hit without Auth header. New user registration
//			}
//		} else {
//			// throw new NoSuchElementException("No user identified!");
//			log.debug("User not identified..Assuming test user..");
//		}
//	}
}
