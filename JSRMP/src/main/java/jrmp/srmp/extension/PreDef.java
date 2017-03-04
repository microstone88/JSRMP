package jrmp.srmp.extension;

/**
 * @author liuj
 *
 */
public class PreDef {

	public enum FileFormat{
		xml, xmcda, txt, csv
	}
	
	public enum FileName{
		alternatives, alternativesComparisons, concordance, criteria, criteriaLinearConstraints, example, lexicography,
		performanceTable, profileConfigs, weights, xmlexample, ranking, preorder, messages 
	}
	
	public enum ElementAttribute{
		id, name
	}
	
	public enum ElementTag{
		//common tags:
		projectReference,
			comment,
		//file alternatives.xml tags:
		alternatives,
			alternative,
				active, 
		//file criteria.xml tags:
		criteria,
			criterion,
				scale,
					quantitative,
						preferenceDirection,
		//file performance.xml tags:
		performanceTable,
			alternativePerformances,
				alternativeID, 
				performance,
					criterionID,
					value,
		//file alternativesComparaisons.xml tags
		alternativesComparisons,
			pairs,
				pair,
					initial,
					terminal
	}
	
}
