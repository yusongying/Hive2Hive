package org.hive2hive.core.process.register;

import java.io.IOException;

import net.tomp2p.futures.FutureDHT;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.log.H2HLogger;
import org.hive2hive.core.log.H2HLoggerFactory;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.network.messages.direct.response.ResponseMessage;
import org.hive2hive.core.process.ProcessStep;

public class CheckIfProfileExistsStep extends ProcessStep {

	private static final H2HLogger logger = H2HLoggerFactory.getLogger(CheckIfProfileExistsStep.class);
	private final String userId;

	public CheckIfProfileExistsStep(String userId) {
		this.userId = userId;
	}

	@Override
	public void start() {
		logger.debug(String.format("Checking if a user profile already exists. user id = '%s'", userId));
		get(userId, H2HConstants.USER_PROFILE);
	}

	@Override
	public void rollBack() {
		// only a get call which has no effect
	}

	@Override
	protected void handleMessageReply(ResponseMessage asyncReturnMessage) {
		// not used
	}

	@Override
	protected void handlePutResult(FutureDHT future) {
		// not used
	}

	@Override
	protected void handleGetResult(FutureDHT future) {
		if (future.getData() == null) {
			logger.debug(String.format("No user profile exists. user id = '%s'", userId));
			RegisterProcess process = (RegisterProcess) super.getProcess();

			// next step: Put the public key of the user into the DHT
			PutPublicKeyStep next = new PutPublicKeyStep(process.getUserProfile());
			getProcess().nextStep(next);
		} else {
			try {
				if (!(future.getData().getObject() instanceof UserProfile)) {
					logger.warn(String.format("Instance of UserProfile expected. key = '%s'", userId));
				}
			} catch (ClassNotFoundException | IOException e) {
				logger.warn(String.format("future.getData().getObject() failed. reason = '%s'",
						e.getMessage()));
			}
			getProcess().rollBack("User profile already exists.");
		}
	}

	@Override
	protected void handleRemovalResult(FutureDHT future) {
		// not used
	}
}