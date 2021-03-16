package ratelimit.api;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import lombok.Getter;

@Getter
public class RateLimiter {

	private int total;

	private String id;

	private int requestLimit;

	private int timeLimitMillis;

	private Vector<TransactionUnit> bucket;

	public static Hashtable<String, RateLimiter> limiterHash = new Hashtable<>();

	public static RateLimiter getRateLimiter(String id, int requestLimit, int timeLimit, boolean forceCreate) {
		RateLimiter limiter = limiterHash.get(id);
		if (forceCreate == true || limiter == null) {
			limiter = new RateLimiter();
			limiter.id = id;
			limiterHash.put(id, limiter);
		}
		limiter.requestLimit = requestLimit;
		limiter.timeLimitMillis = timeLimit * 1000;
		return limiter;
	}

	private RateLimiter() {
		total = 0;
		bucket = new Vector<TransactionUnit>();
	}

	public void processRequest(Runnable r) throws ExceedsLimitException {
		TransactionUnit unit = null;

		synchronized (bucket) {

			long current = System.currentTimeMillis();
			int count = 0;

			Iterator<TransactionUnit> iterator = bucket.iterator();
			while (iterator.hasNext()) {
				TransactionUnit u = iterator.next();
				if (u.isProcessed == true && (timeLimitMillis < 0 || u.startAt + timeLimitMillis < current)) {

					iterator.remove();
					u.r = null;
				} else if (u.startAt + timeLimitMillis >= current) {
					count++;
				}
			}
			if (requestLimit >= 0 && count >= requestLimit) {
				throw new ExceedsLimitException("RateLimit License " + id + " limit " + requestLimit + " per "
						+ timeLimitMillis + " millisecond is Exceeded!");
			}
			total++;
			unit = new TransactionUnit(r);
			bucket.add(unit);
		}

		try {
			unit.r.run();
		} catch (Exception e) {
			throw e;
		} finally {
			unit.isProcessed = true;

		}

	}

	public int requestAlive() {
		return bucket.size();
	}

	public int requestTotal() {
		return total;
	}

	private static class TransactionUnit {
		Runnable r;
		long startAt;
		boolean isProcessed;

		TransactionUnit(Runnable r) {
			this.r = r;
			this.startAt = System.currentTimeMillis();
			isProcessed = false;
		}
	}

	@SuppressWarnings("serial")
	public static class ExceedsLimitException extends Exception {

		public ExceedsLimitException(String msg) {
			super(msg);
		}

	}
}
