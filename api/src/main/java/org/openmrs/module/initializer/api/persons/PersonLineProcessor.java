package org.openmrs.module.initializer.api.persons;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class PersonLineProcessor extends BaseLineProcessor<Person, PersonService> {

	public static final String HEADER_NAME_GIVEN = "Given names";

	public static final String HEADER_NAME_MIDDLE = "Middle names";

	public static final String HEADER_NAME_FAMILY = "Family names";

	public static final String HEADER_GENDER = "Gender";

	public static final String HEADER_BIRTHDATE = "Birthdate";

	public static final String HEADER_ADDRESSES = "Addresses";

	public PersonLineProcessor(String[] headerLine, PersonService ps) {
		super(headerLine, ps);
	}
	
	@Override
	protected Person bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Person pt = service.getPersonByUuid(uuid);
		
		if (pt == null) {
			pt = new Person();
			if (!StringUtils.isEmpty(uuid)) {
				pt.setUuid(uuid);
			}
		}
		
		pt.setVoided(getVoidOrRetire(line.asLine()));
		
		return pt;
	}
	
	@Override
	public Person fill(Person person, CsvLine line) throws IllegalArgumentException {

		{  // name
			String[] nameLists = { line.get(HEADER_NAME_GIVEN), line.get(HEADER_NAME_MIDDLE), line.get(HEADER_NAME_FAMILY) };
			String[][] names = new String[3][]; // String [name field] [name index]
			List<Integer> lengths = new ArrayList<Integer>();
			for (int i = 0; i < nameLists.length; i++) {
				if (nameLists[i] != null) {
					names[i] = nameLists[i].split(",");
					lengths.add(names[i].length);
				}
			}
			if (lengths.size() == 0) {
				throw new IllegalArgumentException("Person must have at least one name.");
			}
			int length;
			if (new HashSet<Integer>(lengths).size() > 1) {
				length = Collections.min(lengths);
				log.warn("Name array length mismatch. Please make sure that all non-empty name fields have the same "
				        + "number of comma-delimited elements. Padding the end of the shorter arrays with null. "
				        + "On line\n" + line);
			} else {
				length = lengths.get(0);
			}
			
			for (int i = 0; i < length; i++) {
				PersonName name = new PersonName(safeGet(names, 0, i), safeGet(names, 1, i), safeGet(names, 2, i));
				if (i == 0) {
					name.setPreferred(true);
				}
				person.addName(name);
			}
		}
		
		person.setGender(line.get(HEADER_GENDER));
		
		String birthdateString = line.get(HEADER_BIRTHDATE);
		if (birthdateString != null && !birthdateString.trim().isEmpty()) {
			DateFormat birthdateFormat = new SimpleDateFormat("y-M-d");
			try {
				Date birthdate = birthdateFormat.parse(birthdateString);
				person.setBirthdate(birthdate);
			}
			catch (ParseException err) {
				throw new IllegalArgumentException("Failed to parse Birthdate. Should be formatted y-m-d.");
			}
		}
		
		String addressesString = line.get(HEADER_ADDRESSES);
		if (addressesString != null && !addressesString.trim().isEmpty()) {
			String[] addressesArray = addressesString.split(";");
			for (String addressString : addressesArray) {
				PersonAddress address = new PersonAddress();
				String[] addressFields = addressString.split(",");
				for (String addressFieldString : addressFields) {
					String[] addressFieldParts = addressFieldString.split(":");
					setAddressField(address, addressFieldParts[0], addressFieldParts[1], line.toString());
				}
				person.addAddress(address);
			}
		}
		
		return person;
	}
	
	private static void setAddressField(PersonAddress target, String addressField, String value, String line) {
		String setMethodName = "set" + addressField.substring(0, 1).toUpperCase() + addressField.substring(1);
		try {
			Method method = target.getClass().getMethod(setMethodName, String.class);
			try {
				method.invoke(target, value);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format("Bad value %s for address field %s for line %s\n%s",
						value, addressField, line, e));
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException(String.format("Unable to set address field %s for line %s\n%s",
						addressField, line, e));
			}
			catch (InvocationTargetException e) {
				throw new RuntimeException(String.format("Unable to set address field %s for line %s\n%s",
						addressField, line, e));
			}
		}
		catch (SecurityException e) {
			throw new RuntimeException(String.format("Unable to set address field %s for line %s\n%s",
					addressField, line, e));
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(String.format("Invalid address field %s in line %s\n%s",
					addressField, line, e));
		}
		
	}
	
	private static <T> T safeGet(T[][] array, int index1, int index2) {
		if (array != null && array.length > index1 && array[index1] != null && array[index1].length > index2) {
			return array[index1][index2];
		} else {
			return null;
		}
	}
}
