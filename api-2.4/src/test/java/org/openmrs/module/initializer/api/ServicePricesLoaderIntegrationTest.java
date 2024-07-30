package org.openmrs.module.initializer.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.billing.api.IPaymentModeService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.billing.ServicePricesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePricesLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {
	
	@Autowired
	private IPaymentModeService paymentModeService;
	
	@Autowired
	private ServicePricesLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts-2.4.xml");
		{
			// To be edited
			PaymentMode paymentMode = new PaymentMode();
			paymentMode.setUuid("526bf278-ba81-4436-b867-c2f6641d060a");
			paymentMode.setName("Antenatal Cash Item");
			paymentMode.setRetired(false);
		}
		
		{
			// To be retired
			PaymentMode paymentMode = new PaymentMode();
			paymentMode.setUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
			paymentMode.setName("Orthopedic Cash Item");
			paymentMode.setRetired(false);
		}
	}
	
	@Test
	public void load_shouldLoadPaymentModesAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creation
		{
			PaymentMode paymentMode = paymentModeService.getByUuid("e168c141-f5fd-4eec-bd3e-633bed1c9606");
			assertNotNull(paymentMode);
			assertEquals("Nutrition Cash Item", paymentMode.getName());
		}
		
		// Verify edition
		{
			PaymentMode paymentMode = paymentModeService.getByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
			assertNotNull(paymentMode);
			assertEquals("Antenatal Cash Item Modified", paymentMode.getName());
		}
		
		// Verify retirement
		{
			PaymentMode paymentMode = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
			Assert.assertTrue(paymentMode.getRetired());
		}
		
		// Verify unretirement
		{
			PaymentMode paymentMode = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
			paymentMode.setRetired(false);
			paymentModeService.save(paymentMode);
			
			PaymentMode unretiredPaymentMode = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
			Assert.assertFalse(unretiredPaymentMode.getRetired());
		}
	}
}
