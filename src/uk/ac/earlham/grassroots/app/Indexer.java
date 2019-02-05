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
package uk.ac.earlham.grassroots.app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.GrassrootsDocumentFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
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
		
	
	private Indexer () {
		in_facets_config = new FacetsConfig ();
	}

	/** Index all text files under a directory. */
	public static void main (String [] args) {
		String usage = "uk.ac.earlham.grassroots.app.Indexer" + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
				+ "in INDEX_PATH that can be searched with SearchFiles";
		String index_dir_name = "index";
		String data_dir_name = null;
		boolean create_index_flag = true;
		
		for (int i = 0; i < args.length; ++ i) {
			if ("-index".equals (args [i])) {
				index_dir_name = args [++ i];
			} else if ("-data".equals (args [i])) {
				data_dir_name = args [++ i];
			} else if ("-update".equals (args [i])) {
				create_index_flag = false;
			}
		}

		if (data_dir_name == null) {
			System.err.println ("Usage: " + usage);
			System.exit (1);
		}

		final Path data_path = Paths.get (data_dir_name);
		if (!Files.isReadable (data_path)) {
			System.err.println ("Document directory '" + data_path.toAbsolutePath () + "' does not exist or is not readable, please check the path");
			System.exit (1);
		}

		Indexer indexer = new Indexer ();
		
		Date start = new Date ();
		System.out.println ("Indexing to directory '" + index_dir_name + "'...");
		
		indexer.run (data_path, index_dir_name, create_index_flag);
	
		Date end = new Date ();
		System.out.println (end.getTime() - start.getTime() + " total milliseconds");
	}

	
	/** Index all text files under a directory. */
	public boolean run (Path data_path, String index_dir_name, boolean create_index_flag) {
		boolean success_flag = false;
		
		if (Files.isReadable (data_path)) {
			try {
				System.out.println("Indexing to directory '" + index_dir_name + "'...");

				Directory index_dir = FSDirectory.open (Paths.get (index_dir_name));
				Analyzer analyzer = new StandardAnalyzer ();
				IndexWriterConfig iwc = new IndexWriterConfig (analyzer);

				
				if (create_index_flag) {
					// Create a new index in the directory, removing any
					// previously indexed documents:
					iwc.setOpenMode (OpenMode.CREATE);
				} else {
					// Add new documents to an existing index:
					iwc.setOpenMode (OpenMode.CREATE_OR_APPEND);
				}

				// Optional: for better indexing performance, if you
				// are indexing many documents, increase the RAM
				// buffer. But if you do this, increase the max heap
				// size to the JVM (eg add -Xmx512m or -Xmx1g):
				//
				// iwc.setRAMBufferSizeMB(256.0);

				IndexWriter writer = new IndexWriter (index_dir, iwc);
				indexDocs (writer, data_path);

				// NOTE: if you want to maximize search performance,
				// you can optionally call forceMerge here. This can be
				// a terribly costly operation, so generally it's only
				// worth it when your index is relatively static (ie
				// you're done adding documents to it):
				//
				// writer.forceMerge(1);

				writer.close ();

			} catch (IOException e) {
				System.err.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			}
		
		
		} else {
			System.err.println ("Document directory '" + data_path.toAbsolutePath () + "' does not exist or is not readable, please check the path");
			return false;
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
	public void indexDocs (final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!indexDoc (writer, file.toString (), attrs.lastModifiedTime().toMillis ())) {
						System.err.println ("Failed to index " + file.toString ());
					}

					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc (writer, path.toString(), Files.getLastModifiedTime(path).toMillis());
		}
	}

	/** Indexes a single document */
	public boolean indexDoc (IndexWriter writer, String filename, long lastModified) {
		boolean success_flag = false;
		FileReader reader = null;
		
		/*
		 * open the file
		 */
		try {
			reader = new FileReader (filename);
		} catch (FileNotFoundException e) {
			System.err.println ("File " + filename + "not found, exception: " + e.getMessage ());
			return false;
		}
		
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
			JSONObject json_obj = (JSONObject) obj;
			GrassrootsDocument grassroots_doc = GrassrootsDocumentFactory.createDocument (json_obj);
			Document doc = grassroots_doc.getDocument ();
			
			try {
				doc = in_facets_config.build (doc);	
			} catch (IOException ioe) {
				System.err.println ("Building faceted document failed for " + filename + " exception: " + ioe.getMessage ());
			}

			if (writer.getConfig ().getOpenMode () == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				System.out.println("adding " + filename);
				
				try {
					writer.addDocument (doc);
					success_flag = true;
				} catch (IOException ioe) {
					System.err.println ("writer.addDocument () failed for " + filename + " exception: " + ioe.getMessage ());
				}
				
			} else {
				// Existing index (an old copy of this document may have been indexed) so
				// we use updateDocument instead to replace the old one matching the exact
				// path, if present:
				System.out.println("updating " + filename);
				
				try {
					writer.updateDocument (new Term ("path", filename), doc);
					success_flag = true;
				} catch (IOException ioe) {
					System.err.println ("writer.updateDocument () failed for " + filename + " exception: " + ioe.getMessage ());
				}
			}
		}
	
		return success_flag;
	}
}