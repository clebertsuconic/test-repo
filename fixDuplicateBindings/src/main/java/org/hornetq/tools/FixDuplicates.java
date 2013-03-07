package org.hornetq.tools;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQBuffers;
import org.hornetq.api.core.Pair;
import org.hornetq.api.core.SimpleString;
import org.hornetq.core.journal.*;
import org.hornetq.core.journal.impl.*;
import org.hornetq.core.persistence.impl.journal.JournalStorageManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FixDuplicates
{

    public static void fixBindings(final String directory, final boolean fix) throws Exception
    {
       SequentialFileFactory bindingsFF = new NIOSequentialFileFactory(directory, null);

       JournalImpl journal = new JournalImpl(1024 * 1024, 2, -1, 0, bindingsFF, "hornetq-bindings", "bindings", 1);

       journal.start();

       final StringBuffer bufferFailingTransactions = new StringBuffer();

       List<RecordInfo> records = new LinkedList<RecordInfo>();
       List<PreparedTransactionInfo> preparedTransactions = new LinkedList<PreparedTransactionInfo>();
       int messageCount = 0;
       Map<Long, Integer> messageRefCounts = new HashMap<Long, Integer>();
       int preparedMessageCount = 0;
       Map<Long, Integer> preparedMessageRefCount = new HashMap<Long, Integer>();
       journal.load(records, preparedTransactions, new TransactionFailureCallback()
       {

          public void failedTransaction(long transactionID, List<RecordInfo> records, List<RecordInfo> recordsToDelete)
          {
             bufferFailingTransactions.append("Transaction " + transactionID + " failed with these records:\n");
             for (RecordInfo info : records)
             {
                bufferFailingTransactions.append("There's a transaction failure with id" + transactionID + "\n");
             }
          }
       }, false);


       HashMap<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>> mapDuplicate =
             new HashMap<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>>();

       HashMap<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>> mapIdentifier =
             new HashMap<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>>();


       for (RecordInfo info : records)
       {
          if (info.getUserRecordType() == 21)
          {
             HornetQBuffer buffer = HornetQBuffers.wrappedBuffer(info.data);

             JournalStorageManager.PersistentQueueBindingEncoding bindingEncoding = new JournalStorageManager.PersistentQueueBindingEncoding();

             bindingEncoding.decode(buffer);

             bindingEncoding.setId(info.id);

             Pair<String, String> key = new Pair<String, String>(bindingEncoding.getAddress().toString(), bindingEncoding.getQueueName().toString());

             HashMap<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>> map;

             if (bindingEncoding.getFilterString() != null && bindingEncoding.getFilterString().toString().equals("__HQX=-1"))
             {
                map = mapIdentifier;
             }
             else
             {
                map = mapDuplicate;
             }

             LinkedList<JournalStorageManager.PersistentQueueBindingEncoding> bindings = map.get(key);

             if (bindings == null)
             {
                bindings = new LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>();
                map.put(key, bindings);
             }

             bindings.add(bindingEncoding);
          }
       }

       for (Map.Entry<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>> iterationElement : mapDuplicate.entrySet())
       {
          if (iterationElement.getValue().size() > 1)
          {
             System.out.println("Duplicate bindings:");
             int duplicateID = 0;
             for (JournalStorageManager.PersistentQueueBindingEncoding binding: iterationElement.getValue())
             {
                System.out.println(binding);
                if (fix)
                {
                   SimpleString renameTo = binding.getQueueName().concat("_duplicate_" + (duplicateID++));

                   System.err.println("subscription/queue: " + binding.getAddress() + "/" + binding.getQueueName() + " being renamed to " + renameTo);


                   JournalStorageManager.PersistentQueueBindingEncoding newRecord = new
                         JournalStorageManager.PersistentQueueBindingEncoding(renameTo,
                                                               binding.getAddress(),
                                                               binding.getFilterString());

                   journal.appendDeleteRecord(binding.id, true);

                   journal.testCompact();

                   journal.appendAddRecord(binding.id, (byte) 21, newRecord, true);
                }
             }
          }
       }

       for (Map.Entry<Pair<String, String>, LinkedList<JournalStorageManager.PersistentQueueBindingEncoding>> iterationElement : mapIdentifier.entrySet())
       {
          if (iterationElement.getValue().size() > 1)
          {
             System.out.println("queue " + iterationElement.getKey().getA() + "/" + iterationElement.getKey().getB() + " with " + iterationElement.getValue().size() + " duplicates on identifiers");

             if (fix)
             {
                boolean first = true;
                for (JournalStorageManager.PersistentQueueBindingEncoding duplicateEncoding: iterationElement.getValue())
                {
                   if (!first)
                   {
                      journal.appendDeleteRecord(duplicateEncoding.getId(), true);
                   }
                   first = false;
                }
             }
          }
       }

       if (fix) journal.testCompact();

       journal.stop();

    }
    public static void main( String[] args )
    {
       if (args.length != 2)
       {
          System.err.println("Usage java -cp fixBindigns-1.0.jar:hornetq-core.jar:netty.jar FixDuplicates <bindins-directory> [fix | nofix]");
          System.exit(-1);
       }

       boolean fix = false;

       if (args[1].equals("fix"))
       {
          fix = true;
       }
       else if (args[1].equals("nofix"))
       {
          fix = false;
       }
       else
       {
          System.err.println("Unkown argument: " + args[1]);
          System.exit(-1);
       }

       try
       {
         FixDuplicates.fixBindings(args[0], fix);
       }
       catch (Throwable e)
       {
          e.printStackTrace();
       }
    }
}
