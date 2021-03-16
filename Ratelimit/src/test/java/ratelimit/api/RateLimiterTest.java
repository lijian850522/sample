package ratelimit.api;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ratelimit.api.RateLimiter.ExceedsLimitException;
import sample.RatelimitApplication;

@SpringBootTest(classes = RatelimitApplication.class)
@RunWith(SpringRunner.class)
public class RateLimiterTest {

	@Test
	public void checkNotExceeded() {
		RateLimiter rateLimiter = RateLimiter.getRateLimiter("test", 100, 1, true);
		try {
			for (int i = 0; i < 100; i++) {
				rateLimiter.processRequest(() -> {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
//			Thread.sleep(3000);
//			for (int i = 0; i < 100; i++) {
//				rateLimiter.processRequest(() -> {
//				});
//			}
		} catch (ExceedsLimitException e) {
			fail("Limit Exceeded!");
		}

	}

	@Test
	public void checkExceeded() {
		RateLimiter rateLimiter = RateLimiter.getRateLimiter("test", 100, 1, true);
		try {
			for (int i = 0; i < 200; i++) {
				rateLimiter.processRequest(() -> {
				});
			}
		} catch (ExceedsLimitException e) {
			return;
		}
		fail("Limit Not Exceeded!");
	}

	@Test
	public void checkUnlimited() {
		RateLimiter rateLimiter = RateLimiter.getRateLimiter("test", -1, 1, true);
		try {
			for (int i = 0; i < 1000; i++) {
				rateLimiter.processRequest(() -> {
				});
			}
		} catch (ExceedsLimitException e) {
			fail("Limit Exceeded!");
		}
	}

}
