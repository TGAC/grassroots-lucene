/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 * This is based upon the code at 
 * 
 * https://lucene.apache.org/core/7_6_0/demo/src-html/org/apache/lucene/demo/IndexFiles.html
 */
package uk.ac.earlham.grassroots.app.lucene;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.GrassrootsDocumentFactory;
import uk.ac.earlham.grassroots.document.MongoDocument;
import uk.ac.earlham.grassroots.document.util.LuceneDocumentWrapper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing. Run
 * it with no command-line arguments for usage information.
 */
public class Indexer {
	private FacetsConfig in_facets_config;
		
	class IndexResult {
		int ir_total_records;	
		int ir_num_succeeded;
		String ir_path;
		
		IndexResult (String path) {
			ir_total_records = 0;
			ir_num_succeeded = 0;
			ir_path = path;
		}
		
		void AddSuccess () {
			++ ir_total_records;
			++ ir_num_succeeded;
		}

		void AddFailure () {
			++ ir_total_records;
		}
	
		JSONObject asJSON () {
			JSONObject obj = new JSONObject ();
			
			obj.put ("path", ir_path);
			obj.put ("successes", ir_num_succeeded);
			obj.put ("total", ir_total_records);
			
			return obj;					
		}
	}
	
	
	class MyFileVisitor extends SimpleFileVisitor<Path> {
	
		IndexResult mfv_results;
		IndexWriter mfv_index_writer;
		TaxonomyWriter mfv_tax_writer;
		
		MyFileVisitor (IndexWriter index_writer, TaxonomyWriter tax_writer, IndexResult totals) {
			mfv_index_writer = index_writer;
			mfv_tax_writer = tax_writer;
			mfv_results = totals;
		}
		
		@Override
		public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) throws IOException {
			
			System.out.println ("Indexing " + file.toString () + "... ");

			LuceneDocumentWrapper wrapper = new LuceneDocumentWrapper ();
			indexFile (wrapper, mfv_index_writer, mfv_tax_writer, file.toString (), attrs.lastModifiedTime().toMillis (), mfv_results);
			
			return FileVisitResult.CONTINUE;								
		}
		
		IndexResult getIndexingResults () {
			return mfv_results;
		}

	}
	
	
	private Indexer () {
		in_facets_config = new FacetsConfig ();
	}

	/** Index all text files under a directory. */
	public static void main (String [] args) {
		String usage = "uk.ac.earlham.grassroots.app.Indexer" + " [-index INDEX_PATH] [-data DOCS_PATH] [-tax TAXONOMY_PATH] [-update]\n\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
				+ "in INDEX_PATH that can be searched with SearchFiles";
		String index_dir_name = "index";
		String data_dir_name = null;
		String taxonomy_dir_name = "taxonomy";
		String results_filename = null;
		boolean create_index_flag = true;
		
		for (int i = 0; i < args.length; ++ i) {
			if ("-index".equals (args [i])) {
				index_dir_name = args [++ i];
			} else if ("-data".equals (args [i])) {
				data_dir_name = args [++ i];
			} else if ("-tax".equals (args [i])) {
				taxonomy_dir_name = args [++ i];
			} else if ("-update".equals (args [i])) {
				create_index_flag = false;
			} else if ("-results".equals (args [i])) {
				results_filename = args [++ i];
			} else if ("-out".equals (args [i])) {
				try {
					System.setOut (new PrintStream (args [++ i]));
				} catch (FileNotFoundException e) {
					System.out.println ("Couldn't set output stream to " + args [i] + "e: " + e);
					e.printStackTrace();
				}
			} else if ("-err".equals (args [i])) {
				try {
					System.setErr (new PrintStream (args [++ i]));
				} catch (FileNotFoundException e) {
					System.out.println ("Couldn't set error stream to " + args [i] + "e: " + e);
					e.printStackTrace();
				}
			}
		}

		if (data_dir_name == null) {
			System.out.println ("Usage: " + usage);
			System.exit (1);
		}

		final Path data_path = Paths.get (data_dir_name);
		if (!Files.isReadable (data_path)) {
			System.err.println ("Document directory '" + data_path.toAbsolutePath () + "' does not exist or is not readable, please check the path");
			System.exit (1);
		}

		Indexer indexer = new Indexer ();
		
		Date start = new Date ();
		
		indexer.run (data_path, index_dir_name, taxonomy_dir_name, results_filename, create_index_flag);
	
		Date end = new Date ();
		System.out.println (end.getTime() - start.getTime() + " total milliseconds");
	}

	
	/** Index all text files under a directory. */
	public boolean run (Path data_path, String index_dir_name, String taxonomy_dir_name, String results_filename, boolean create_index_flag) {
		boolean success_flag = true;
		
		if (Files.isReadable (data_path)) {
			Directory index_dir = null;
			Directory taxonomy_dir = null;

			System.out.println("Indexing to directory '" + index_dir_name + "'...");

			try {
				index_dir = FSDirectory.open (Paths.get (index_dir_name));
			} catch (IOException ioe) {
				System.err.println ("Error opening index dir " + index_dir_name + " e: " + ioe.getMessage());			    																							
			}

			try {
				taxonomy_dir = FSDirectory.open (Paths.get (taxonomy_dir_name));
			} catch (IOException ioe) {
				System.err.println ("Error opening index dir " + taxonomy_dir_name + " e: " + ioe.getMessage());			    																							
			}

			
			if ((index_dir != null) && (taxonomy_dir != null)) {
				Analyzer analyzer = new StandardAnalyzer ();
				IndexWriterConfig iwc = new IndexWriterConfig (analyzer);
				IndexWriter index_writer = null;
			    DirectoryTaxonomyWriter tax_writer = null;

				
				try {
					index_writer = new IndexWriter (index_dir, iwc);
				    tax_writer = new DirectoryTaxonomyWriter (taxonomy_dir);
				} catch (IOException ioe) {
					System.err.println ("Error opening writers: " + ioe.getMessage());			    																		
				}
				
				
				if ((index_writer != null) && (tax_writer != null)) {
					// Optional: for better indexing performance, if you
					// are indexing many documents, increase the RAM
					// buffer. But if you do this, increase the max heap
					// size to the JVM (eg add -Xmx512m or -Xmx1g):
					//
					// iwc.setRAMBufferSizeMB(256.0);


					if (create_index_flag) {
						try {
							index_writer.deleteAll ();
						} catch (IOException ioe) {
							success_flag = false;
							System.err.println ("Clearing existing indexes failed: " + ioe.getMessage());			    							
						}
					}

					if (success_flag) {
				    	IndexResult results = null;
				    	
				    	try {
				    		results = indexDocs (index_writer, tax_writer, data_path);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				    	
				    	if (results != null) {
				    		
				    		if (results_filename != null) {
				    			JSONObject results_json = results.asJSON ();
				    			
				    			if (results_json != null) {
				    				FileWriter results_writer = null;
				    						
				    						
				    				try {
										results_writer = new FileWriter (results_filename);
									} catch (IOException e) {
										System.err.println ("Failed to open " + results_filename + " for saving results");
									}					    				
	
				    				
				    				if (results_writer != null) {
				    					try {
				    						results_json.writeJSONString (results_writer);
				    				    	success_flag = true;
				    				    	
				    					} catch (IOException ioe) {
											System.err.println ("writeJSONString threw an error: " + ioe.getMessage());	
				    					} finally {
				    						try {
												results_writer.close ();
											} catch (IOException ioe) {
												System.err.println ("results_writer.close (): " + ioe.getMessage());	
											}
				    					}
				    				}
				    			}
				    		}
				    			
				    			
				    	}
				    	
				    }
					
					// NOTE: if you want to maximize search performance,
					// you can optionally call forceMerge here. This can be
					// a terribly costly operation, so generally it's only
					// worth it when your index is relatively static (ie
					// you're done adding documents to it):
					//
					// writer.forceMerge(1);

					try {
					    tax_writer.close ();
					} catch (IOException ioe) {
						System.err.println ("tax_writer.close (): " + ioe.getMessage());			    							
					}

					try {
					    index_writer.close ();
					} catch (IOException ioe) {
						System.err.println ("index_writer.close (): " + ioe.getMessage());			    													
					}
					
				} else {
					System.err.println ("Error: index dir '" + index_dir.toString () +  " tax dir '" + taxonomy_dir.toString () + " index_writer " + index_writer + " tax_writer " + tax_writer);
				}	

				
			} else {
				System.err.println ("Error: index dir '" + index_dir.toString () + " tax dir '" + taxonomy_dir.toString ());
			}
			
		} else {
			System.err.println ("Document directory '" + data_path.toAbsolutePath () + "' does not exist or is not readable, please check the path");
		}

		return success_flag;
	}
	
	/**
	 * Indexes the given file using the given writer, or if a directory is given,
	 * recurses over files and directories found under the given directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For good
	 * throughput, put multiple documents into your input file(s). An example of
	 * this is in the benchmark module, which can create "line doc" files, one
	 * document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * @param writer Writer to the index where the given file/dir info will be
	 *               stored
	 * @param path   The file to index, or the directory to recurse into to find
	 *               files to index
	 * @throws IOException If there is a low-level I/O error
	 */
	public IndexResult indexDocs (final IndexWriter index_writer, TaxonomyWriter tax_writer, Path path) throws IOException {
		LuceneDocumentWrapper wrapper = new LuceneDocumentWrapper ();
		IndexResult totals = new IndexResult (path.toString ());
		
		if (Files.isDirectory (path)) {
			
			System.out.println ("Walking " + path.toString ());
			MyFileVisitor visitor = new MyFileVisitor (index_writer, tax_writer, totals);
			
			Files.walkFileTree (path, visitor);			
			
		} else {
			indexFile (wrapper, index_writer, tax_writer, path.toString(), Files.getLastModifiedTime (path).toMillis (), totals);
		}
		
		return totals;
	}

	/** Indexes a single file */
	public void indexFile (LuceneDocumentWrapper wrapper, IndexWriter index_writer, TaxonomyWriter tax_writer, String filename, long lastModified, IndexResult results) {
		FileReader reader = null;
		
		/*
		 * open the file
		 */
		try {
			reader = new FileReader (filename);
		} catch (FileNotFoundException e) {
			System.err.println ("File " + filename + "not found, exception: " + e.getMessage ());
		}

		
		if (reader != null) {
			JSONParser parser = new JSONParser ();
			Object obj = null;
			
			try {
				obj = parser.parse (reader);
			} catch (IOException ioe) {
				System.err.println ("Failed to load JSON from " + filename + " exception: " + ioe.getMessage ());
			} catch (ParseException pe) {
				System.err.println ("Failed to parse JSON from " + filename + " exception: " + pe.getMessage ());			
			}
			
			if (obj != null) {
				
				if (obj instanceof JSONObject) {
					JSONObject json_obj = (JSONObject) obj;

					indexObj (json_obj, 1, 1, index_writer, tax_writer, filename, wrapper, results);
				} else if (obj instanceof JSONArray) {
					JSONArray json_array = (JSONArray) obj;
					
					final int size = json_array.size ();
					
					for (int i = 0; i < size; ++ i) {
						obj = json_array.get (i);
						
						if (obj instanceof JSONObject) {
							JSONObject json_obj = (JSONObject) obj;
							
							indexObj (json_obj, i + 1, size, index_writer, tax_writer, filename, wrapper, results);

							wrapper.clear();
						}
					}
				}
			}								
		}
	}


	private void indexObj (JSONObject json_obj, int obj_index, int total, IndexWriter index_writer, TaxonomyWriter tax_writer, String filename, LuceneDocumentWrapper wrapper, IndexResult results) {
		boolean success_flag = false;
		GrassrootsDocument grassroots_doc = null;

		//System.out.println ("initial json:\n" + json_obj);

		
		try {
			grassroots_doc = GrassrootsDocumentFactory.createDocument (json_obj, wrapper);
		} catch (Exception e) {
			System.err.println ("GrassrootsDocumentFactory.createDocument () failed for " + json_obj + " exception: " + e.getMessage ());
		}
		
		if (grassroots_doc != null) {
			Document doc = wrapper.getDocument ();
			String id_str = grassroots_doc.getUniqueId ();

			
			if (id_str != null) {
				//System.out.println ("initial doc:\n" + doc);

				wrapper.addString (GrassrootsDocument.GD_ID_KEY, id_str);
				
				wrapper.process ();
				
				try {
					doc = in_facets_config.build (tax_writer, doc);	
				} catch (Exception e) {
					System.err.println ("Building faceted document failed for " + json_obj + "\n " + "doc " + doc + "\n exception: " + e.getMessage ());
					doc = null;
				}

				if (doc != null) {
					// Existing index (an old copy of this document may have been indexed) so
					// we use updateDocument instead to replace the old one matching the exact
					// path, if present:
					System.out.println ("updating " + filename + ": " + obj_index + "/" + total);
					
					try {
						index_writer.updateDocument (new Term (GrassrootsDocument.GD_ID_KEY, id_str), doc);
						success_flag = true;
					} catch (IOException ioe) {
						System.err.println ("writer.updateDocument () failed for " + filename + " exception: " + ioe.getMessage ());
					}
				}
			
			} else {
				System.err.println ("Not updating doc: No unique id for " + json_obj);					
			}

				
		} else {
			System.err.println("no document from " + json_obj);
			
		}

		if (success_flag) {
			results.AddSuccess ();
		} else {
			results.AddFailure ();
		}
	}
}