package org.zkoss.zklargelivelist.model;

import java.util.ArrayList;
import java.util.List;

import bz.voter.management.Division;
import bz.voter.management.Election;
import bz.voter.management.Pledge;
import bz.voter.management.utils.FilterType;

import org.zkoss.zul.AbstractListModel;

public abstract class AbstractElectionVotersPagingListModel<T> extends AbstractListModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6613208067174831719L;
	
	private int _startPageNumber;
	private int _pageSize;
	private int _itemStartNumber;
	private Division _division;
	private Election _election;
    private Pledge _pledge;
	private String _searchString;
    private boolean _voted;
	
	//internal use only
	private List<T> _items = new ArrayList<T>();

	public AbstractElectionVotersPagingListModel(){
	}
	

	public AbstractElectionVotersPagingListModel(Election election, Division division, int startPageNumber, int pageSize) {
		super();
		
		initialize(election,division, startPageNumber, pageSize);

		_items = getPageData(_itemStartNumber, _pageSize);
	}


	public AbstractElectionVotersPagingListModel(String searchString,Election election,Division division, int startPageNumber, int pageSize) {
		super();
		
		initialize(election,division, startPageNumber, pageSize);
		_searchString = searchString;
		
		_items = getPageData(_searchString, _itemStartNumber, _pageSize);
	}


    public AbstractElectionVotersPagingListModel(FilterType filterType, Object filterObject, Election election, Division division, int startPageNumber, int pageSize){
        super();

        initialize(election, division, startPageNumber, pageSize);
        _items = getPageData(filterType, filterObject, _itemStartNumber, _pageSize);
    }


    public AbstractElectionVotersPagingListModel(FilterType filterType, Object filterObject, Election election, Division division, boolean voted, int startPageNumber, int pageSize){
        super();

        initialize(election, division, voted,startPageNumber, pageSize);
        _items = getPageData(filterType, filterObject, _itemStartNumber, _pageSize);
    }
        


	private void initialize(Election election,Division division, int startPageNumber, int pageSize){
		this._division = division;
		this._startPageNumber = startPageNumber;
		this._pageSize = pageSize;
		this._itemStartNumber = startPageNumber * pageSize;
		this._election = election;
	}

    private void initialize(Election election, Division division, boolean voted,int startPageNumber, int pageSize){
        this._voted = voted;
        initialize(election, division, startPageNumber, pageSize);
    }
	
	public abstract int getTotalSize();
	protected abstract List<T> getPageData(int itemStartNumber, int pageSize);
	protected abstract List<T> getPageData(String search, int itemStartNumber, int pageSize);
	protected abstract List<T> getPageData(FilterType filterType, Object filterObject, int itemStartNumber, int pageSize);
	
	@Override
	public Object getElementAt(int index) {
		return _items.get(index);
	}

	@Override
	public int getSize() {
		return _items.size();
	}
	
	public int getStartPageNumber() {
		return this._startPageNumber;
	}
	
	public int getPageSize() {
		return this._pageSize;
	}
	
	public int getItemStartNumber() {
		return _itemStartNumber;
	}

	public Election getElection(){
		return _election;
	}


    public Pledge getPledge(){
        return _pledge;
    }

    protected void setPledge(Pledge pledge){
        this._pledge = pledge;
    }


    protected boolean getVoted(){
        return _voted;
    }

    protected void setVoted(boolean voted){
        this._voted = voted;
    }

	public String getSearchString(){
		return _searchString;
	}

	public Division getDivision(){
		return _division;
	}
}
