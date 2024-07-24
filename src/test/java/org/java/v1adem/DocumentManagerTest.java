package org.java.v1adem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.v1adem.DocumentManager;
import org.v1adem.DocumentManager.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        DocumentManager.Document document = Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(Author.builder().id("author1").name("Author One").build())
                .build();

        Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Test Title", savedDocument.getTitle());
        assertEquals("Test Content", savedDocument.getContent());
        assertEquals("author1", savedDocument.getAuthor().getId());
        assertNotNull(savedDocument.getCreated());
    }

    @Test
    void testSaveExistingDocument() {
        Document document = Document.builder()
                .id("123")
                .title("Test Title")
                .content("Test Content")
                .author(Author.builder().id("author1").build())
                .build();

        documentManager.save(document);
        document.setContent("Updated Content");

        Document updatedDocument = documentManager.save(document);

        assertEquals("123", updatedDocument.getId());
        assertEquals("Updated Content", updatedDocument.getContent());
    }

    @Test
    void testFindById() {
        Document document = Document.builder()
                .id("123")
                .title("Test Title")
                .content("Test Content")
                .author(Author.builder().id("author1").build())
                .build();

        Document savedDocument = documentManager.save(document);

        Optional<Document> foundDocument = documentManager.findById("123");

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument.getId(), foundDocument.get().getId());
    }

    @Test
    void testSearchByTitlePrefix() {
        Document doc1 = Document.builder()
                .title("Test title 1")
                .content("Content 1")
                .author(Author.builder().id("author1").build())
                .build();

        Document doc2 = Document.builder()
                .title("Test title 2")
                .content("Content 2")
                .author(Author.builder().id("author2").build())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Test"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    void testSearchByContent() {
        Document doc1 = Document.builder()
                .title("Doc1")
                .content("This is a test document.")
                .author(Author.builder().id("author1").build())
                .build();

        Document doc2 = Document.builder()
                .title("Doc2")
                .content("Another document for testing.")
                .author(Author.builder().id("author2").build())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("test"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    void testSearchByAuthorId() {
        Document doc1 = Document.builder()
                .title("Doc1")
                .content("Content 1")
                .author(Author.builder().id("author1").build())
                .build();

        Document doc2 = Document.builder()
                .title("Doc2")
                .content("Content 2")
                .author(Author.builder().id("author2").build())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("author1"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("author1", results.getFirst().getAuthor().getId());
    }

    @Test
    void testSearchByCreatedDateRange() {
        Document doc1 = Document.builder()
                .title("Doc1")
                .content("Content 1")
                .author(Author.builder().id("author1").build())
                .created(Instant.now().minusSeconds(100000))
                .build();

        Document doc2 = Document.builder()
                .title("Doc2")
                .content("Content 2")
                .author(Author.builder().id("author2").build())
                .created(Instant.now())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(Instant.now().minusSeconds(1800))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Doc2", results.getFirst().getTitle());
    }
}
