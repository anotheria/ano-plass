package net.anotheria.anoplass.api.util.paging;

import java.util.ArrayList;
import java.util.List;

/**
 * PagingControl.
 *
 * @author another
 * @version $Id: $Id
 */
public class PagingControl {
	/**
	 * PagingControl 'currentPage'
	 */
	private int currentPage;
	/**
	 * PagingControl 'numberOfElements'
	 */
	private int numberOfElements;
	/**
	 * PagingControl 'elementsPerPage'
	 */
	private int elementsPerPage;
	/**
	 * PagingControl 'maxEntries'
	 */
	private int maxEntries;
	/**
	 * PagingControl 'moreHitsAvailable'
	 */
	private boolean moreHitsAvailable;
	/**
	 * PagingControl 'elements'
	 */
	private List<PagingElement> elements;

	/**
	 * Default constructor.
	 */
	public PagingControl(){
		this(1,1,1);
	}

	/**
	 * Constructor.
	 *
	 * @param aCurrentPage current page number
	 * @param anElementsPerPage elements per page
	 * @param aNumberOfElements totalAmount of elements
	 */
	public PagingControl(int aCurrentPage, int anElementsPerPage, int aNumberOfElements){
		this(aCurrentPage, anElementsPerPage, aNumberOfElements, false);
	}

	/**
	 * Constructor.
	 *
	 * @param aCurrentPage current page number
	 * @param anElementsPerPage elements per page
	 * @param aNumberOfElements totalAmount of elements
	 * @param aMoreHitsAvailable boolean param
	 */
	public PagingControl(int aCurrentPage, int anElementsPerPage, int aNumberOfElements, boolean aMoreHitsAvailable){
		currentPage = aCurrentPage;
		elementsPerPage = anElementsPerPage;
		numberOfElements = aNumberOfElements;
		elements = new ArrayList<PagingElement>();
		moreHitsAvailable = aMoreHitsAvailable;
		setup();
	}
		
	/** {@inheritDoc} */
	@Override
	public String toString(){
		return "currentPage:"+currentPage+", numberOfElements:"+numberOfElements+", elementsPerPage:"+elementsPerPage+ 
				", maxEntries:"+maxEntries+", moreHitsAvailable:"+moreHitsAvailable;
	}

	/**
	 * Init.
	 */
	void setup(){
		int numberOfPages = getNumberOfPages();
		for (int i=1; i<=numberOfPages; i++){
			if (i==currentPage){
				elements.add(new PagingCurrentPage(""+i));
			}else{
				boolean addLink = numberOfPages <= 7 ? true : addLink(i, currentPage, numberOfPages);
				if (addLink)
					elements.add(new PagingLink(""+i, i));
				if (!addLink){
					if (!elements.get(elements.size()-1).isSeparator())
						elements.add(new Separator());
				}
			}
		}
	}
	
	private boolean addLink(int page, int selectedPage, int numberOfPages){
		if (page==1 || page==numberOfPages)
			return true;
		if (((selectedPage>page) && (selectedPage-page <= 2)) || ((page>selectedPage) && (page-selectedPage<=2)))
			return true;
			
		return false;
		
	}

	/**
	 * Return number of elements.
	 *
	 * @return int value
	 */
	public int getNumberOfElements(){
		return numberOfElements;
	}

	/**
	 * Returns number of pages.
	 *
	 * @return int value
	 */
	public int getNumberOfPages(){
		int numberOfPages = numberOfElements / elementsPerPage;
		if (numberOfPages*elementsPerPage<numberOfElements)
			numberOfPages++;
		return numberOfPages;
	}

	/**
	 * <p>Getter for the field <code>elements</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<PagingElement> getElements(){
		return elements;
	}

	/**
	 * True if there is some next page, false otherwise.
	 *
	 * @return boolean value
	 */
	public boolean hasNext(){
		return currentPage < getNumberOfPages();
	}
	/**
	 * True if there is some next page, false otherwise.
	 *
	 * @return boolean value
	 */
	public boolean isHasNext(){
		return hasNext();
	}

	/**
	 * True if there is some previous page, false otherwise.
	 *
	 * @return boolean value
	 */
	public boolean hasPrevious(){
		return currentPage>=2;
	}
	/**
	 * True if there is some previous page, false otherwise.
	 *
	 * @return boolean value
	 */
	public boolean isHasPrevious(){
		return hasPrevious();
	}

	/**
	 * <p>Getter for the field <code>elementsPerPage</code>.</p>
	 *
	 * @return size of page (quantity of item to be shown on page).
	 */
	public int getElementsPerPage(){
		return elementsPerPage;
	}
	/**
	 * True if current page is the last, false otherwise.
	 *
	 * @return boolean value
	 */
	public boolean isLast(){
		return currentPage == getNumberOfPages();
	}

	/**
	 * True if current page is the first, false otherwise.
	 *
	 * @return boolean value
	 */
	public boolean isFirst(){
		return currentPage == 1;
	}

	
	/**
	 * <p>Getter for the field <code>maxEntries</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMaxEntries() {
		return maxEntries;
	}

	/**
	 * <p>Setter for the field <code>maxEntries</code>.</p>
	 *
	 * @param aMaxEntries a int.
	 */
	public void setMaxEntries(int aMaxEntries) {
		maxEntries = aMaxEntries;
	}

	/**
	 * <p>isMoreHitsAvailable.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isMoreHitsAvailable() {
		return moreHitsAvailable;
	}
	
	/**
	 * <p>getCurrentPageNumber.</p>
	 *
	 * @return a int.
	 */
	public int getCurrentPageNumber(){
		return currentPage;
	}

	/**
	 * Return next page number
	 *
	 * @return int value
	 */
	public int getNextPageNumber(){
		return currentPage + 1;
	}
	/**
	 * Return previous page number
	 *
	 * @return int value
	 */
	public int getPreviousPageNumber(){
		return currentPage-1;
	}
	
}
