package org.zkoss.zklargelivelist.model;

import java.util.List;

//import org.zkoss.zklargelivelist.database.DatabaseInformation;
//import org.zkoss.zklargelivelist.database.User;

//public class PagingListModel extends AbstractPagingListModel<User> {
public class PagingListModel extends AbstractPagingListModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4370353438941246687L;

	
	public PagingListModel(int startPageNumber, int pageSize) {
		super(startPageNumber, pageSize);
	}

	@Override
	//protected List<User> getPageData(int itemStartNumber, int pageSize) {
	protected List getPageData(int itemStartNumber, int pageSize) {
		//return DatabaseInformation.dao.selectWithLimit(itemStartNumber, pageSize);
	}

	@Override
	public int getTotalSize() {
		//return DatabaseInformation.dao.getUserCount();
		return 20
	}


}
