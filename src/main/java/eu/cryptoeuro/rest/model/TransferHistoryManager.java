package eu.cryptoeuro.rest.model;

import eu.cryptoeuro.rest.model.TransferHistory;
import eu.cryptoeuro.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;

import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

public class TransferHistoryManager {

	private static TransferHistoryManager instance;
	private static int checkedOut = 0;
	private static CacheAccess<String, TransferHistory> transferHistoryCache;

	@Autowired
	private TransferService transferService;

	private TransferHistoryManager () {
		//try {
			transferHistoryCache = JCS.getInstance("transferHistoryCache");
		//} catch (Exception e) {
		//}
	}
	public static TransferHistoryManager getInstance() {
		synchronized (TransferHistoryManager.class) {
			if (instance == null) { instance = new TransferHistoryManager(); }
		}
		synchronized (instance) {
			instance.checkedOut++;
		}
		return instance;
	}
	public TransferHistory getTransferHistory(String address) {
		return getTransferHistory(address, true);
	}
	public TransferHistory getTransferHistory(String address, boolean fromCache) {
		TransferHistory transferHistory = null;

		if (fromCache) {
			transferHistory = transferHistoryCache.get("TransferHistory"+address);
		}

		if (transferHistory == null) {
			transferHistory = loadTransferHistory(address);
		}

		return transferHistory;
	}
	public TransferHistory loadTransferHistory(String address){
		TransferHistory transferHistory = new TransferHistory();
		transferHistory.address = address;
		transferHistory.lastBlock = 0;
		transferHistory.transferList = Arrays.asList();
		//transferHistory.transferList = transferService.getTransfersForAccount(address);
		return transferHistory;

	}
	public void storeTransferHistory(TransferHistory transferHistory) {
//		try {
			transferHistoryCache.remove("TransferHistory"+transferHistory.address);
			transferHistoryCache.put("TransferHistory"+transferHistory.address, transferHistory);
//		} catch (Exception e) {
//		}
	}


}
