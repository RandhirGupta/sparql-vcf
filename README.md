sparql-vcf
==========

Some code written at biohackathon 2014 to explore methods for querying data in a VCF file using SPARQL. These approaches are based on a methodology for creating triples "on-the-fly" using dedictated indexes to optimise execution times. 

Implementation 1. is based on Jerven's sparql-bed (https://github.com/JervenBolleman/sparql-bed) code that uses Sesame API. 

Implementation 2. is something I wrote with Jena to find out how property functions work. The idea here is that for a specific use-case, like querying for variants by chromomse coordiantes in a VCF file, we can introduce a special property that executes some code to run the query. The idea would be a hybrid approach to "on-the-fly" where you have most of the triples in a regular triple store, but the special predicates is used to optimize queries similar to how existing triple stores support lucene search. Not entirely sure this is the best approach, but it was fun playing wih it.  

Example query get all variants on chromsome Y in range 2600000:2700000

~~~

PREFIX otf:<http://onthefly.com/>
SELECT * WHERE {
 ?feature otf:vcf-chromo-query \"Y,2600000,2700000\" .
}

~~~
