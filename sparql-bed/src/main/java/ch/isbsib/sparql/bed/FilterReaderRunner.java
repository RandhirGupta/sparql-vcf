package ch.isbsib.sparql.bed;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FilterReaderRunner implements Runnable {
    private static final Logger log = LoggerFactory
            .getLogger(FilterReaderRunner.class);
    private final BlockingQueue<Statement> statements;
    private final Resource subj;
    private final Value obj;
    private final URI pred;
    private final VCFFileReader reader;
    volatile boolean done = false;
    private final File bedFile;
    private final ValueFactory vf;
    private final Pattern comma = Pattern.compile(",");

    public FilterReaderRunner(File vcfFile, File vcfIndex, Resource subj, URI pred, Value obj,
                              BlockingQueue<Statement> statements, ValueFactory vf) {

        this.vf = vf;

        this.reader = new VCFFileReader(vcfFile, vcfIndex, true);

        this.subj = subj;
        this.pred = pred;
        this.obj = obj;
        this.statements = statements;
        this.bedFile = vcfFile;
    }

    @Override
    public void run() {
        long lineNo = 0;
        String filePath = "file:///" + bedFile.getAbsolutePath();
        CloseableIterator<VariantContext> iter;
        try {
            VCFToTripleConverter conv = new VCFToTripleConverter(vf, pred);
            iter = reader.iterator();
            while (iter.hasNext()) {
                VariantContext feature = iter.next();
                for (Statement statement : filter(conv.convertFeatureToTriples(
                        feature))) {
                    try {
                        while (!offer(statement) && !this.done)
                            ;

                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                }

            }
        } finally {
                reader.close();

            done = true;
        }
    }

    protected boolean offer(Statement statement) throws InterruptedException {
        boolean offer = statements.offer(statement, 100, TimeUnit.MICROSECONDS);
        return offer;
    }

    private List<Statement> filter(List<Statement> statements) {
        List<Statement> filtered = new ArrayList<Statement>();
        for (Statement toFilter : statements) {
            Resource subject = toFilter.getSubject();
            Resource predicate = toFilter.getPredicate();
            Value object = toFilter.getObject();
            if (matches(subject, subj) && matches(predicate, pred)
                    && matches(object, obj)) {
                filtered.add(toFilter);
            }
        }
        return filtered;
    }

    protected boolean matches(Value subject, Value subj) {
        return subject.equals(subj) || subj == null;
    }

}