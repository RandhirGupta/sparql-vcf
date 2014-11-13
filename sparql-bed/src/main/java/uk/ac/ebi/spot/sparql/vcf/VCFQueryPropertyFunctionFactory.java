package uk.ac.ebi.spot.sparql.vcf;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import com.hp.hpl.jena.sparql.pfunction.PFuncSimple;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionFactory;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 13/11/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class VCFQueryPropertyFunctionFactory implements PropertyFunctionFactory {

    private VCFFileReader vcfFileReader;

    public VCFQueryPropertyFunctionFactory() {
        this.vcfFileReader = new VCFFileReader(
                new File(VCFQueryPropertyFunctionFactory.class.getClassLoader()
                				.getResource("example.vcf.gz").getFile()),
                new File (VCFQueryPropertyFunctionFactory.class.getClassLoader()
                				.getResource("example.vcf.gz.tbi").getFile()), true);

    }

    public PropertyFunction create(String s) {

        return new PFuncSimple()
        {
            @Override
            public QueryIterator execEvaluated(Binding binding, Node node, Node node2, Node node3, ExecutionContext executionContext) {

                List<Binding> bindings = new ArrayList<Binding>() ;

                if (node3.isLiteral()) {

                    String query = node3.getLiteral().toString();
//                    System.out.println(executionContext.getExecutor().);
//                    System.out.println(executionContext.getContext().keys());

                    List<String> params = Arrays.asList(query.split(","));


                    CloseableIterator<VariantContext> context = vcfFileReader.query(params.get(0), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)));

                    while (context.hasNext()) {
                        VariantContext c = context.next();
                        Binding b = BindingFactory.binding(binding, Var.alloc(node), NodeFactory.createURI("http://www.identifers.org/variant/" + c.getID()));
                        bindings.add(b);
                    }
                }

                return new QueryIterPlainWrapper(bindings.iterator(), executionContext) ;

            }
        };
    }



}
