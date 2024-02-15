package il.ac.tau.cs.sw1.ex5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BigramModel 
{
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	
	String[] mVocabulary;
	int[][] mBigramCounts;
	
	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException
	{
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);
	}
	
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException
	{ // Q 1
		String [] vocab = new String [MAX_VOCABULARY_SIZE];
		File fromFile = new File(fileName);
		BufferedReader br =	new BufferedReader(new FileReader(fromFile));
		
		int wordCounter = 0;	//Count legal words in language
		for (String line = br.readLine(); line != null && wordCounter<MAX_VOCABULARY_SIZE; line = br.readLine()) 
		{
			String [] lineArr = line.split("\\s");
			for (int i = 0; i<lineArr.length && wordCounter<MAX_VOCABULARY_SIZE; i++) 
			{
				//No need to add word if it is already in vocabulary
				if (!isInVocab(lineArr[i].toLowerCase(), vocab)) 
				{
					//Legal word i: Contains English character
					if (isEnglish(lineArr[i])) 
					{
						vocab[wordCounter] = lineArr[i].toLowerCase();
						wordCounter++;
					}
					//Legal word ii: Word is an integer
					else if (isInteger(lineArr[i]) && !isInVocab(SOME_NUM, vocab)) 
					{
						vocab[wordCounter] = SOME_NUM;
						wordCounter++;
					}
				}
			}
		}
		br.close();
		return Arrays.copyOf(vocab, wordCounter);
	}
	
	private boolean isInVocab(String word, String[] arr) 
	{
		for (int i=0; i<arr.length; i++) 
			if (arr[i] != null && arr[i].equals(word)) 
				return true;
		return false;
	}

	private boolean isEnglish(String word) 
	{
		for (int i=0; i<word.length(); i++) 
			if (Character.isLetter(word.charAt(i))) 
				return true;
		return false;
	}

	private boolean isInteger(String str) 
	{
		for (int i=0; i<str.length(); i++) 
			if (!Character.isDigit(str.charAt(i))) 
				return false;
		return true;
	}
	
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException
	{ // Q - 2
		File fromFile = new File(fileName);
		BufferedReader br =	new BufferedReader(new FileReader(fromFile));
		int [][] bigramCounts = new int [vocabulary.length][vocabulary.length];
		for (String line = br.readLine(); line != null ; line = br.readLine()) 
		{
			String [] lineArr = line.split("\\s");
			for (int j= 0; j<lineArr.length-1; j++) 
			{
				int idx1, idx2;
				if (isInteger(lineArr[j])) 
					idx1 = indexOf(SOME_NUM, vocabulary);
				else 
					idx1 = indexOf(lineArr[j].toLowerCase(), vocabulary);
				
				if (isInteger(lineArr[j+1])) 
					idx2 = indexOf(SOME_NUM, vocabulary);
				else 
					idx2 = indexOf(lineArr[j+1].toLowerCase(), vocabulary);
				
				if (idx1 != ELEMENT_NOT_FOUND && idx2 != ELEMENT_NOT_FOUND) 
					bigramCounts[idx1][idx2]++;
			}
		}
		br.close();
		return bigramCounts;
	}
	
	private int indexOf(String word, String[] arr) 
	{
		for (int i=0; i<arr.length; i++) 
			if (arr[i].equals(word))
				return i;
		return ELEMENT_NOT_FOUND;
	}
	
	
	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException
	{ // Q-3
		//COUNTS file
		FileWriter fw = new FileWriter(fileName+COUNTS_FILE_SUFFIX);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i=0; i<mBigramCounts.length; i++) 
			for(int j=0; j<mBigramCounts.length; j++) 
				if(mBigramCounts[i][j]!=0) 
				{
					bw.write(i+"," + j + ":"+mBigramCounts[i][j]);
					bw.newLine();
				}
		bw.flush();
		bw.close();
		fw.close();
				
		//VOC file
		fw = new FileWriter(fileName+VOC_FILE_SUFFIX);
		bw = new BufferedWriter(fw);
		bw.write(mVocabulary.length + " words");
		bw.newLine();
		for (int i=0; i<mVocabulary.length; i++) 
		{
			bw.write(i + "," + mVocabulary[i]);
			bw.newLine();
		}
		bw.flush();
		bw.close();
		fw.close();		
	}
	
	
	
	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException
	{ // Q - 4
		//VOC file
		File fromFile = new File(fileName+VOC_FILE_SUFFIX);
		BufferedReader br =	new BufferedReader(new FileReader(fromFile));
		String firstLine = br.readLine();
		String [] firstLineArr = firstLine.split("\\s");
		int vocabLength = Integer.parseInt(firstLineArr[0]);
		mVocabulary = new String [vocabLength];
		int i = 0;
		for (String line = br.readLine(); line != null; line = br.readLine()) 
		{
			String [] lineArr = line.split(",");
			mVocabulary[i] = lineArr[1];
			i++;
		}
		br.close();
		
		//COUNTS file
		mBigramCounts = new int [vocabLength][vocabLength];
		fromFile = new File(fileName+COUNTS_FILE_SUFFIX);
		br = new BufferedReader(new FileReader(fromFile));
		for (String line = br.readLine(); line != null; line = br.readLine()) 
		{
			String [] lineArr = line.split(":");
			String[] indexArr = lineArr[0].split(",");
			mBigramCounts[Integer.parseInt(indexArr[0])][Integer.parseInt(indexArr[1])] = Integer.parseInt(lineArr[1]);
		}
		br.close();
	}

	
	
	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word)
	{  // Q - 5
		return indexOf(word, mVocabulary);
	}
	
	
	
	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2)
	{ //  Q - 6
		if (getWordIndex(word1) != -1 && getWordIndex(word2) != -1) 
			return mBigramCounts[getWordIndex(word1)][getWordIndex(word2)];
		return 0;
	}
	
	
	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word)
	{ //  Q - 7
		int max = 0;
		String maxWord = null;
		for (int i = 0; i<mVocabulary.length; i++)
			if(getBigramCount(word, mVocabulary[i]) > max) 
			{
				max = getBigramCount(word, mVocabulary[i]);
				maxWord = mVocabulary[i];
			}
		return maxWord;
	}
	
	
	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence)
	{  //  Q - 8
		String [] words = sentence.split("\\s");
		if (words.length == 1) 
			return !(getWordIndex(words[0]) == ELEMENT_NOT_FOUND);

		for(int i=0; i<words.length-1; i++) 
			if(getBigramCount(words[i], words[i+1]) <1) 
				return false;
		return true;
	}
	
	
	
	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2)
	{ //  Q - 9
		double sumAB = 0.0;
		double sumAi = 0.0;
		double sumBi = 0.0;
		for (int i =0; i<arr1.length; i++) 
		{
			sumAB += arr1[i]*arr2[i];
			sumAi += arr1[i]*arr1[i];
			sumBi += arr2[i]*arr2[i];
		}
		
		if (sumAi == 0 || sumBi == 0) 
			return -1;
		else
			return (sumAB)/( (Math.sqrt(sumAi))*(Math.sqrt(sumBi)));
	}

	
	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized), 
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word)
	{ //  Q - 10
		if (mVocabulary.length == 1) 
			return mVocabulary[0];
		
		int wordIndex = getWordIndex(word);
		int [] bigramCounts = mBigramCounts[wordIndex];
		double max = -2;
		String closestWord = null;
		for (int i=0; i<mVocabulary.length; i++) 
			if(i != wordIndex) 
			{
				double sim = calcCosineSim(bigramCounts, mBigramCounts[i]);
				if (sim > max) 
				{
					max = sim;
					closestWord = mVocabulary[i];
				}
			}
		return closestWord;
	}
}
