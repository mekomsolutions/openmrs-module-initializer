package org.openmrs.module.initializer.api.providerroles;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This provides an abstraction layer over determining what service methods should be called for working with
 * ProviderRole objects, and what object type a ProviderRole is instantiated into.
 * This supports both core ProviderRole objects in OpenMRS 2.8.2+, while also supporting the original ProviderRole
 * object defined in providermanagement < 4.0.0 and the revised ProviderManagementProviderRole type that is present
 * in providermanagement 4.0.0+ and which extends the OpenMRS 2.8 ProviderRole class
 */
public class ProviderRoleServiceAdapter {
	
	protected final static Logger log = LoggerFactory.getLogger(ProviderRoleServiceAdapter.class);

	/**
	 * @return the ProviderManagementService from providermanagement if found, otherwise the ProviderService from core
	 */
	public Object getService() {
		String providerManagementService = "org.openmrs.module.providermanagement.api.ProviderManagementService";
		String providerService = ProviderService.class.getName();
		Class<?> clazz = loadFirstFoundClass(providerManagementService, providerService);
		return Context.getRegisteredComponents(clazz).get(0);
	}

	/**
	 * @return the providermanagement.ProviderRole class if found (providermanagement < 4.0.0, if not found, then
	 * the providermanagement.ProviderManagementProviderRole class if found (providermanagement >= 4.0.0).
	 * if not found, then this returns the core ProviderRole object if found.
	 * If none found, this throws an exception, as no support exists for ProviderRoles at runtime
	 */
	public ProviderRoleAdapter newProviderRoleAdapter() {
		String providerRole1 = "org.openmrs.module.providermanagement.ProviderRole";
		String providerRole2 = "org.openmrs.module.providermanagement.ProviderManagementProviderRole";
		String providerRole3 = "org.openmrs.ProviderRole";
		Class<?> clazz = loadFirstFoundClass(providerRole1, providerRole2, providerRole3);
		try {
			OpenmrsMetadata providerRoleInstance = (OpenmrsMetadata) clazz.newInstance();
			return new ProviderRoleAdapter(providerRoleInstance);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param uuid the uuid of the provider role
	 * @return the provider role instance returned from the appropriate service
	 */
	public OpenmrsMetadata getProviderRoleByUuid(String uuid) {
		Object providerRole = invokeMethod(getService(), "getProviderRoleByUuid", uuid);
		return (OpenmrsMetadata) providerRole;
	}

	/**
	 * Saves the providerRole contained within the given ProviderRoleAdapter
	 * @param providerRole the ProviderRoleAdapter instance that contains the ProviderRole to save
	 */
	public void saveProviderRole(ProviderRoleAdapter providerRole) {
		invokeMethod(getService(), "saveProviderRole", providerRole.getProviderRole());
	}
	
	/**
	 * Takes in an array of class names and attempts to load each in order. Returns the first that loads
	 * successfully, otherwise throws an exception
	 */
	private Class<?> loadFirstFoundClass(String... classesToLoad) {
		for (String className : classesToLoad) {
			try {
				return OpenmrsClassLoader.getInstance().loadClass(className);
			}
			catch (Exception e) {
				log.debug("Class {} not found", className);
			}
		}
		throw new RuntimeException("None of the matching classes were found: " + Arrays.toString(classesToLoad));
	}
	
	/**
	 * Retrieves the method with the given name from the given class. If the method name is not found or
	 * is not unique, throws an exception
	 */
	private Object invokeMethod(Object object, String methodName, Object... arguments) {
		Class<?> clazz = object.getClass();
		Class<?>[] parameterTypes = new Class[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			parameterTypes[i] = arguments[i].getClass();
		}
		try {
			Method method = clazz.getMethod(methodName, parameterTypes);
			return method.invoke(object, arguments);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
