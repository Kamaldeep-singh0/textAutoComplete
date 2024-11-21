import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TextAutocomplete extends JFrame {
    private final Trie trie;
    private JTextField inputField;
    private JList<String> suggestionList;

    public TextAutocomplete() {
        trie = new Trie();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Real-Time Text Autocomplete");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        suggestionList = new JList<>();
        suggestionList.setFont(new Font("Arial", Font.PLAIN, 16));

        // Adding a document listener to update suggestions dynamically
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });

        // Add components to the frame
        add(new JLabel("Type something:"), BorderLayout.NORTH);
        add(inputField, BorderLayout.CENTER);
        add(new JScrollPane(suggestionList), BorderLayout.SOUTH);

        // Preload the Trie with example words
        preloadDictionary();

        setVisible(true);
    }

    private void updateSuggestions() {
        String input = inputField.getText().toLowerCase();
        if (input.isEmpty()) {
            suggestionList.setListData(new String[0]);
            return;
        }
        List<String> suggestions = trie.getWordsStartingWith(input);
        suggestionList.setListData(suggestions.toArray(new String[0]));
    }

    private void preloadDictionary() {
        // Add some example words to the dictionary
        String[] words = {
                "apple", "application", "appreciate", "banana", "basket", "cat",
                "dog", "dolphin", "elephant", "fish", "grape", "happy", "house",
                "java", "javascript", "kangaroo", "lion", "monkey", "orange", "pear",
                "zebra"
        };
        for (String word : words) {
            trie.insert(word);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextAutocomplete::new);
    }

    // Trie implementation for fast prefix search
    static class Trie {
        private final TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        // Insert a word into the Trie
        public void insert(String word) {
            TrieNode current = root;
            for (char c : word.toCharArray()) {
                current.children.putIfAbsent(c, new TrieNode());
                current = current.children.get(c);
            }
            current.isEndOfWord = true;
        }

        // Get all words starting with a given prefix
        public List<String> getWordsStartingWith(String prefix) {
            List<String> results = new ArrayList<>();
            TrieNode current = root;

            for (char c : prefix.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return results; // No words with this prefix
                }
                current = current.children.get(c);
            }

            collectAllWords(current, new StringBuilder(prefix), results);
            return results;
        }

        // Recursively collect all words from a given node
        private void collectAllWords(TrieNode node, StringBuilder prefix, List<String> results) {
            if (node.isEndOfWord) {
                results.add(prefix.toString());
            }
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                prefix.append(entry.getKey());
                collectAllWords(entry.getValue(), prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }

        // TrieNode class
        static class TrieNode {
            Map<Character, TrieNode> children = new HashMap<>();
            boolean isEndOfWord;
        }
    }
}
