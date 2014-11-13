package ch.isbsib.sparql.bed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import htsjdk.variant.variantcontext.VariantContext;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;

public class VCFToTripleConverter {
	private final boolean rdftype;
	private final boolean faldobegin;
	private final boolean faldoend;

	public VCFToTripleConverter(ValueFactory vf, URI... preds) {
		super();
		this.vf = vf;
		List<URI> predList = Arrays.asList(preds);
		boolean tempType = predList.contains(RDF.TYPE);
		boolean tempfaldobegin = predList.contains(FALDO.BEGIN_PREDICATE);
		boolean tempfaldoend = predList.contains(FALDO.END_PREDICATE);

		if (predList.isEmpty() || predList.contains(null)) {
			tempType = true;
			tempfaldobegin = true;
			tempfaldoend = true;
		}
		rdftype = tempType;
		faldobegin = tempfaldobegin;
		faldoend = tempfaldoend;
		// type = predList.contains(RDF.TYPE) || predList.isEmpty() ||
		// predList.contains(null);
	}

	private final ValueFactory vf;

    public List<Statement> convertFeatureToTriples(VariantContext feature) {
        List<Statement> stats = new ArrayList<Statement>();
       		String recordPath = "http://www.identifiers.org/variant/" + feature.getID();
       		URI recordId = vf.createURI(recordPath);
       		URI alignStartId = vf.createURI(recordPath + "#start");
       		URI alignEndId = vf.createURI(recordPath + "#end");

       		add(stats, recordId, VCF.CHROMOSOME, feature.getChr());

       		if (rdftype) {
       			rdfTypesForFeature(stats, recordId, alignStartId, alignEndId);
       		}
       		if (faldobegin) {
       			add(stats, recordId, FALDO.BEGIN_PREDICATE, alignStartId);
       		}
       		add(stats, alignStartId, FALDO.POSTION_PREDICATE, feature.getStart());
       		add(stats, alignStartId, FALDO.REFERENCE_PREDICATE, feature.getChr());

       		if (faldoend) {
       			add(stats, recordId, FALDO.END_PREDICATE, alignEndId);
       		}
       		add(stats, alignEndId, FALDO.POSTION_PREDICATE, feature.getEnd());
       		add(stats, alignEndId, FALDO.REFERENCE_PREDICATE, feature.getChr());
//       		if (feature instanceof BEDFeature) {
//       			stats.addAll(convertLineToTriples(filePath, (BEDFeature) feature,
//       					lineNo));
//       		}
       		return stats;
    }

//	public List<Statement> convertLineToTriples(String filePath,
//			Feature feature, long lineNo) {
//		List<Statement> stats = new ArrayList<Statement>(28);
//		String recordPath = filePath + '/' + lineNo;
//		URI recordId = vf.createURI(recordPath);
//		URI alignStartId = vf.createURI(recordPath + "#start");
//		URI alignEndId = vf.createURI(recordPath + "#end");
//
//		add(stats, recordId, VCF.CHROMOSOME, feature.getChr());
//
//		if (rdftype) {
//			rdfTypesForFeature(stats, recordId, alignStartId, alignEndId);
//		}
//		if (faldobegin) {
//			add(stats, recordId, FALDO.BEGIN_PREDICATE, alignStartId);
//		}
//		add(stats, alignStartId, FALDO.POSTION_PREDICATE, feature.getStart());
//		add(stats, alignStartId, FALDO.REFERENCE_PREDICATE, feature.getChr());
//
//		if (faldoend) {
//			add(stats, recordId, FALDO.END_PREDICATE, alignEndId);
//		}
//		add(stats, alignEndId, FALDO.POSTION_PREDICATE, feature.getEnd());
//		add(stats, alignEndId, FALDO.REFERENCE_PREDICATE, feature.getChr());
//		if (feature instanceof BEDFeature) {
//			stats.addAll(convertLineToTriples(filePath, (BEDFeature) feature,
//					lineNo));
//		}
//		return stats;
//	}

	protected void rdfTypesForFeature(List<Statement> stats, URI recordId,
			URI alignStartId, URI alignEndId) {
		add(stats, recordId, RDF.TYPE, VCF.FEATURE_CLASS);
		add(stats, recordId, RDF.TYPE, FALDO.REGION_CLASS);
		add(stats, alignStartId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
		add(stats, alignEndId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
	}

//	private List<Statement> convertLineToTriples(String filePath,
//			BEDFeature feature, long lineNo) {
//		List<Statement> stats = new ArrayList<Statement>(28);
//		String recordPath = filePath + '/' + lineNo;
//		URI recordId = vf.createURI(recordPath);
//		if (feature.getName() != null) // name
//			add(stats, recordId, RDFS.LABEL, feature.getName());
//		if (feature.getScore() != Float.NaN) // score
//			add(stats, recordId, VCF.SCORE, feature.getScore());
//		if (rdftype)
//			addStrandedNessInformation(stats, feature, recordId);
//		// we skip position 6,7 and 8 as these are colouring instructions
//
//		for (Exon exon : feature.getExons()) {
//			convertExon(feature, stats, recordPath, recordId, exon);
//		}
//		return stats;
//	}

//	protected void convertExon(BEDFeature feature, List<Statement> stats,
//			String recordPath, URI recordId, Exon exon) {
//		String exonPath = recordPath + "/exon/" + exon.getNumber();
//		URI exonId = vf.createURI(exonPath);
//		URI beginId = vf.createURI(exonPath + "/begin");
//		URI endId = vf.createURI(exonPath + "/end");
//		add(stats, recordId, VCF.EXON, endId);
//		if (rdftype) {
//			add(stats, exonId, RDF.TYPE, FALDO.REGION_CLASS);
//			add(stats, endId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
//		}
//		if (faldobegin) {
//			add(stats, exonId, FALDO.BEGIN_PREDICATE, beginId);
//		}
//		add(stats, beginId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
//		add(stats, beginId, FALDO.POSTION_PREDICATE, exon.getCdStart());
//		add(stats, beginId, FALDO.REFERENCE_PREDICATE, feature.getChr());
//		if (faldoend) {
//			add(stats, exonId, FALDO.END_PREDICATE, endId);
//		}
//		add(stats, endId, FALDO.POSTION_PREDICATE, exon.getCdEnd());
//		add(stats, endId, FALDO.REFERENCE_PREDICATE, feature.getChr());
//	}

//	protected void addStrandedNessInformation(List<Statement> statements,
//			VariantContext feature, URI alignEndId) {
//
//		if (Strand.POSITIVE == feature.getcStrand()) {
//			add(statements, alignEndId, RDF.TYPE,
//					FALDO.FORWARD_STRAND_POSITION_CLASS);
//		} else if (Strand.NEGATIVE == feature.getStrand()) {
//			add(statements, alignEndId, RDF.TYPE,
//					FALDO.REVERSE_STRANDED_POSITION_CLASS);
//		} else {
//			add(statements, alignEndId, RDF.TYPE, FALDO.STRANDED_POSITION_CLASS);
//		}
//
//	}

	private void add(List<Statement> statements, URI subject, URI predicate,
			String string) {
		add(statements, subject, predicate, vf.createLiteral(string));

	}

	private void add(List<Statement> statements, URI subject, URI predicate,
			int string) {
		add(statements, subject, predicate, vf.createLiteral(string));

	}

	private void add(List<Statement> statements, URI subject, URI predicate,
			float string) {
		add(statements, subject, predicate, vf.createLiteral(string));

	}

	private void add(List<Statement> statements, Resource subject,
			URI predicate, Value object) {
		statements.add(vf.createStatement(subject, predicate, object));
	}


}
