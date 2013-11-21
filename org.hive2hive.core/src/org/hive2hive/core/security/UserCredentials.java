package org.hive2hive.core.security;

import org.hive2hive.core.H2HConstants;

/**
 * This stores a user's credentials. Do not change the password or the PIN manually by using
 * setters but rather define both parameters from scratch. The PIN needs to be unique per-user per-password.
 * 
 * @author Christian
 * 
 */
public final class UserCredentials {

	private final String userId;
	private final String password;
	private final String pin;
	private String locationCache = null;

	public UserCredentials(String userId, String password, String pin) {
		this.userId = userId;
		this.password = password;
		this.pin = pin;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getPin() {
		return pin;
	}

	/**
	 * Calculates the location for this {@link UserCredentials}. Once calculated, the location gets cached and
	 * directly returned on further invokes.
	 * 
	 * @return The location key associated with this credentials.
	 */
	public String getProfileLocationKey() {

		if (locationCache != null)
			return locationCache;

		// concatenate PIN + PW + UserId
		String location = new StringBuilder().append(pin).append(password).append(userId).toString();

		// create fixed salt based on location
		byte[] fixedSalt = PasswordUtil.generateFixedSalt(location.getBytes());

		// hash the location
		byte[] locationKey = PasswordUtil.generateHash(location.toCharArray(), fixedSalt);

		locationCache = new String(locationKey, H2HConstants.ENCODING_CHARSET);
		return locationCache;
	}
}