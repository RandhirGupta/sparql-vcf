package ch.isbsib.sparql.bed;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import uk.ac.ebi.spot.sparql.vcf.VCFQueryPropertyFunctionFactory;

/**
 * @author Simon Jupp
 * @date 13/11/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * Example of using a custom property function with Jena to query a VCF file
 * This example assumes you would have some hybrid approach where all data is loaded
 * into regular triple store
 *
 */
public class VCFTest {

    public static void main(String[] args) {
        final Dataset ds = DatasetFactory.createMem();


        Graph graph = GraphFactory.createPlainGraph();
        Model model = ModelFactory.createModelForGraph(graph);
        ds.addNamedModel("http://onthefly.com/vcf", model);

        String query1 = "PREFIX otf:<http://onthefly.com/>\n"
       			+ "SELECT * WHERE {" +
                "?feature otf:vcf-chromo-query \"Y,2600000,2700000\" . \n" +
                "}";

        final PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ds.getContext());
        reg.put("http://onthefly.com/vcf-chromo-query", new VCFQueryPropertyFunctionFactory());
        PropertyFunctionRegistry.set(ds.getContext(), reg);

        Query query = QueryFactory.create(query1);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, ds);
        ResultSet results = qe.execSelect();

        // Output query results
        ResultSetFormatter.out(System.out, results, query);

        // Important - free up resources used running the query
        qe.close();
    }
}