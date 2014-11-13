package ch.isbsib.sparql.bed;

import static ch.isbsib.sparql.bed.VCFFileStoreSchema.FILE;
import static ch.isbsib.sparql.bed.VCFFileStoreSchema.INDEX;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailImplConfigBase;

public class VCFFileConfig extends SailImplConfigBase {

    private String file;
    private String index;

	public VCFFileConfig() {
		super(VCFFileStoreFactory.SAIL_TYPE);
	}

    public String getFile() {
   		return file;
   	}
    public String getIndex() {
   		return index;
   	}

	@Override
	public void parse(Graph graph, Resource implNode)
			throws SailConfigException {
		super.parse(graph, implNode);

		try {
            Literal persistValue = GraphUtil.getOptionalObjectLiteral(graph,
         					implNode, FILE);
            Literal persistValue2 = GraphUtil.getOptionalObjectLiteral(graph,
         					implNode, INDEX);
			if (persistValue != null) {
				try {
                    setFile((persistValue).stringValue());
                    setIndex((persistValue2).stringValue());
				} catch (IllegalArgumentException e) {
					throw new SailConfigException("Boolean value required for "
							+ FILE + " property, found " + persistValue);
				}
			}
		} catch (GraphUtilException e) {
			throw new SailConfigException(e.getMessage(), e);
		}
	}

	private void setFile(String stringValue) {
		this.file = stringValue;

	}

    private void setIndex(String stringValue) {
   		this.index = stringValue;

   	}
	@Override
	public Resource export(Graph graph)
	{
		Resource implNode = super.export(graph);

		if (this.file != null) {
            graph.add(implNode, FILE, graph.getValueFactory().createLiteral(file));
            graph.add(implNode, INDEX, graph.getValueFactory().createLiteral(index));
		}

		return implNode;
	}
}
