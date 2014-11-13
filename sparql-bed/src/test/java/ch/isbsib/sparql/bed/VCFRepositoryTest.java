package ch.isbsib.sparql.bed;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;

public class VCFRepositoryTest extends TestCase {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
    private File vcfFile = null;
    private File indexFile = null;
	private File dataDir = null;

	@Before
	public void setUp() {
		// try {
        vcfFile = new File(VCFRepositoryTest.class.getClassLoader()
				.getResource("example.vcf.gz").getFile());
        indexFile = new File(VCFRepositoryTest.class.getClassLoader()
				.getResource("example.vcf.gz.tbi").getFile());
		dataDir = folder.newFolder("data.dir");
		//
		// } catch (IOException e) {
		// fail();
		// }
	}

	@After
	public void tearDown() {

		dataDir.delete();
	}

	String query1 = "PREFIX faldo:<http://biohackathon.org/resource/faldo#>\n"
			+ " SELECT DISTINCT ?feature ?beginP ?endP WHERE { " +
            " ?feature <http://biohackathon.org/resource/bed#Chromosome> \"Y\" ;" +
            "         faldo:begin [ faldo:position ?beginP] ;" +
            "         faldo:end [ faldo:position ?endP] ." +
            "         filter (?beginP > 2600000) ." +
            "         filter (?endP < 2700000) ." +
            "}";

	@Test
	public void testRecordNumber() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {

        assertTrue(vcfFile.exists());
        assertTrue(indexFile.exists());
		VCFFileStore rep = new VCFFileStore();
		rep.setDataDir(dataDir);
		rep.setSamFile(vcfFile, indexFile);
		rep.setValueFactory(new ValueFactoryImpl());
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query1);
		TupleQueryResult eval = pTQ.evaluate();
		for (int i = 0; i < 1; i++) {
//			assertTrue(eval.hasNext());
//			assertNotNull(eval.next());
		}

        while (eval.hasNext()) {
              BindingSet s = eval.next();
//            Value subject = s.getValue("s");
            Value feature = s.getValue("feature");
            Value begin = s.getValue("beginP");
            Value end = s.getValue("endP");
//            Value object = s.getValue("o");

//            System.out.println("value of ?subject: " + subject);
            System.out.println("value of ?feature: " + feature);
            System.out.println("value of ?begin: " + begin);
            System.out.println("value of ?end: " + end);
//            System.out.println("value of ?object: " + object);
         }
//		assertFalse(eval.hasNext());
	}

	String query2 = "PREFIX bed:<"
			+ VCF.NAMESPACE
			+ "> SELECT (COUNT(?score) AS ?countScore) WHERE {?s bed:score ?score}";

	@Test
	public void testRecordNumberViaCount() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {

        assertTrue(vcfFile.exists());
        assertTrue(indexFile.exists());
		VCFFileStore rep = new VCFFileStore();
		rep.setDataDir(dataDir);
		rep.setSamFile(vcfFile, indexFile);
		rep.setValueFactory(new ValueFactoryImpl());
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query2);
		TupleQueryResult eval = pTQ.evaluate();

		assertTrue(eval.hasNext());
		BindingSet next = eval.next();
		assertNotNull(next);
		assertEquals(next.getBinding("countScore").getValue().stringValue(),
				"9");
	}

	String query3 = "PREFIX bed:<"
			+ VCF.NAMESPACE
			+ ">\n"
			+ "PREFIX rdf:<"
			+ RDF.NAMESPACE
			+ ">\n"
			+ "PREFIX faldo:<"
			+ FALDO.NAMESPACE
			+ ">\n"
			+ "SELECT (AVG(?length) as ?avgLength) \n"
			+ " WHERE {?s faldo:begin ?b ; faldo:end ?e . ?b faldo:position ?begin . ?e faldo:position ?end . BIND(abs(?end - ?begin) as ?length)} GROUP BY ?s";

	@Test
	public void testAverageReadLengthNumber() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {

//		assertTrue(newFile.exists());
		VCFFileStore rep = new VCFFileStore();
		rep.setDataDir(dataDir);
		rep.setSamFile(vcfFile, indexFile);
		rep.setValueFactory(new ValueFactoryImpl());
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query3);
		TupleQueryResult eval = pTQ.evaluate();

		assertTrue(eval.hasNext());
		BindingSet next = eval.next();
		assertNotNull(next);
		Binding lb = next.getBinding("avgLength");
		assertEquals("", "1166", lb.getValue()
				.stringValue());
	}
}
