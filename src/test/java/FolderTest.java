package tests;

import junit.framework.*;
import com.rssoap.model.Folder;

/**
 * JUnit test case for FolderTest
 */

public class FolderTest extends TestCase {
	//declare reusable objects to be used across multiple tests
	public FolderTest(String name) {
		super(name);
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(FolderTest.class);
	}
	public static Test suite() {
		return new TestSuite(FolderTest.class);
	}
	protected void setUp() {
		//define reusable objects to be used across multiple tests
	}
	protected void tearDown() {
		//clean up after testing (if necessary)
	}

	public void testFolderConstructorAndGetters() {
		String foldername = "Test'folder";
		String foldername2 = "Second test folder";
		int folderid = 344412112;
		int parentid = 122;
		Folder folder1 = new Folder(foldername);
		Folder folder2 = new Folder(folderid, parentid, foldername2);
		assertNotNull("A folder should not be null after construction", folder1);
		assertNotNull("A folder should not be null after construction", folder2);
		assertEquals("A constructed folder should have the right name", "Test'folder", folder1.getTitle());
		assertEquals("A constructed folder should have the right name", "Second test folder", folder2.getTitle());
		assertEquals("A constructed folder should correctly report its id", 344412112, folder2.getId());
		assertEquals("A constructed folder should correctly report its id", 122, folder2.getParentId());
		try {
			folder1.getId();
			fail("A folder made with the 'small' constructor should not successfully report an id");
		}
		catch (IllegalAccessError e) {}
		catch (Exception e) {
			fail ("Encountered error unexpected trying to get id of a folder made without an id: "+e);
		}
		try {
			folder1.getParentId();
			fail("A folder made with the 'small' constructor should not successfully report a parent id");
		}
		catch (IllegalAccessError e) {}
		catch (Exception e) {
			fail ("Encountered error unexpected trying to get parent id of a folder constructed without a parent id: "+e);
		}

	}

	public void testFolderNullConstruction() {
		try {
			Folder folder1 = new Folder(null);
			Folder folder2 = new Folder(1, 1, null);
			folder1.getTitle();
			folder2.getTitle();
		}
		catch (Exception e)
		{
			fail("Creating folders with null titles and getting those null titles should not throw an exception: "+e);
		}

	}

}