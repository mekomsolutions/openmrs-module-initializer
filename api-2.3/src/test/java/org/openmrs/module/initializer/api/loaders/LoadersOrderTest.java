/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.loaders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_3_Test;

public class LoadersOrderTest extends DomainBaseModuleContextSensitive_2_3_Test {
	
	@Test
	public void getLoaders_shouldBeUnequivocallyOrdered() {
		Loader previousLoader = null;
		
		List<Loader> loaders = applicationContext.getBeansOfType(Loader.class).values().stream().sorted()
		        .collect(Collectors.toList());
		
		for (Loader loader : loaders) {
			if (previousLoader == null) {
				previousLoader = loader;
				continue;
			}
			
			previousLoader = assertLoaderOrder(previousLoader, loader);
		}
		
		List<Loader> runtimeLoaders = getService().getLoaders();
		assertThat(runtimeLoaders, everyItem(hasExpectedDomain()));
		assertThat(runtimeLoaders, coversAllDomains());
	}
	
	private Loader assertLoaderOrder(Loader previousLoader, Loader currentLoader) {
		assertThat("Expected " + currentLoader + " to have an order value greater than " + previousLoader
		        + " but the current loader order is " + currentLoader.getOrder() + " and the previous loader had order "
		        + previousLoader.getOrder(),
		    currentLoader.getOrder(), greaterThan(previousLoader.getOrder()));
		return currentLoader;
	}
	
	private static Matcher<List<Loader>> coversAllDomains() {
		return new AllDomainsMatcher();
	}
	
	private static Matcher<Loader> hasExpectedDomain() {
		return new ExpectedDomainMatcher();
	}
	
	private static class AllDomainsMatcher extends TypeSafeDiagnosingMatcher<List<Loader>> {
		
		@Override
		protected boolean matchesSafely(List<Loader> loaders, Description mismatchDescription) {
			Set<String> exclude = new HashSet<>();
			exclude.add(Domain.PAYMENT_MODES.getName());
			exclude.add(Domain.BILLABLE_SERVICES.getName());
			exclude.add(Domain.CASH_POINTS.getName());
			exclude.add(Domain.CONCEPT_REFERENCE_RANGE.getName());
			exclude.add(Domain.FLAGS.getName());
			exclude.add(Domain.FLAG_PRIORITIES.getName());
			exclude.add(Domain.FLAG_TAGS.getName());
			
			boolean result = true;
			Set<String> loaderDomains = loaders.stream().map(Loader::getDomainName).collect(Collectors.toSet());
			for (Domain domain : Domain.values()) {
				if (exclude.contains(domain.getName())) {
					continue;
				}
				if (!loaderDomains.contains(domain.getName())) {
					mismatchDescription.appendText(" no loader for domain ").appendText(domain.toString())
					        .appendText(" was found");
					result = false;
				}
			}
			
			return result;
		}
		
		@Override
		public void describeTo(Description description) {
			
		}
	}
	
	private static class ExpectedDomainMatcher extends TypeSafeDiagnosingMatcher<Loader> {
		
		@Override
		protected boolean matchesSafely(Loader item, Description mismatchDescription) {
			try {
				Arrays.stream(Domain.values()).filter(d -> d.getName().equals(item.getDomainName())).findFirst()
				        .orElseThrow(IllegalArgumentException::new);
			}
			catch (IllegalArgumentException e) {
				mismatchDescription.appendText("has a domain name ").appendValue(item.getDomainName())
				        .appendText(" that is not a recognised domain name");
				return false;
			}
			
			return true;
		}
		
		@Override
		public void describeTo(Description description) {
			description.appendText("the domain is a valid domain");
		}
	}
}
