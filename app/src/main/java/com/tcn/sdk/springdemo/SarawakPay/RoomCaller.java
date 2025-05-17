package com.tcn.sdk.springdemo.SarawakPay;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class RoomCaller {

    private final RoomDatabase roomDatabase;
    private final RoomConfigurationDao dbDao;

    public RoomCaller(Context context) {
        roomDatabase = RoomDatabase.getSarawakPayDatabase(context);
        dbDao = roomDatabase.sarawakPayConfigurationDao();
    }

    //----------for Debug Log

    public void insertDebugLog(final DebugLog debugLog) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.insertDbDebugLog(debugLog);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    public List<DebugLog> getAllDebugLog() {
        final List<DebugLog>[] debugLogs = new List[]{new ArrayList<>()};

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    debugLogs[0] = dbDao.getDbAllDebugLog();
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }

        return debugLogs[0];
    }

    public DebugLog getLatestDebugLog() {
        final DebugLog[] debugLogs = {null};

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    debugLogs[0] = dbDao.getDbLatestDebugLog();
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }

        return debugLogs[0];
    }

    public DebugLog getDebugLogById(final int id) {
        final DebugLog[] debugLogs = {null};

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    debugLogs[0] = dbDao.getDbDebugLogById(id);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }

        return debugLogs[0];
    }

    public void updateDebugLogEntry(final DebugLog debugLog) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.updateDbDebugLogEntry(debugLog);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    public void deleteAllDebugLogByBatch(final String batchNo) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.deleteDbAllDebugLogByBatch(batchNo);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    public void deleteAllDebugLog() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.deleteDbAllDebugLog();
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    //----------for Transaction

    public void insertTransaction(final Transaction transaction) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.insertDbTransaction(transaction);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    public Transaction getLatestTransaction() {
        final Transaction[] transactions = {null};

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    transactions[0] = dbDao.getDbLatestTransaction();
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }

        return transactions[0];
    }

    public List<Transaction> getAllTransaction() {
        final List<Transaction>[] transactions = new List[]{new ArrayList<>()};

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    transactions[0] = dbDao.getDbAllTransaction();
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }

        return transactions[0];
    }

    public Transaction getTransactionByInvoiceNoAndBatchNo(final String invoiceNo, final String batchNo) {
        final Transaction[] transactions = {null};

        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    transactions[0] = dbDao.getDbTransactionByInvoiceNoAndBatchNo(invoiceNo, batchNo);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }

        return transactions[0];
    }

    public List<Transaction> getAllSuccessSaleVoidTransaction() {
        List<Transaction> successSaleVoidTransactions = new ArrayList<>();

        for (Transaction t : getAllTransaction()) {
            if ((t.getType().toLowerCase().contains("sale") || t.getType().toLowerCase().contains("void")) && (t.getStatus().toLowerCase().trim().equals("success") && t.getVoidedAt().trim().isEmpty())) {
                successSaleVoidTransactions.add(t);
            }
        }

        return successSaleVoidTransactions;
    }

    public Transaction getLatestSuccessSaleTransaction() {
        ArrayList<Transaction> transactions = new ArrayList<>(getAllTransaction());

        for (Transaction t : transactions) {
            if (t.getType().toLowerCase().trim().equals("sale") && t.getStatus().toLowerCase().trim().equals("success") && t.getVoidedAt().trim().isEmpty()) {
                return t;
            }
        }

        return null;
    }

    public List<Transaction> getAllSuccessSaleVoidTransactionByScheme(String schemeCode) {
        List<Transaction> schemeTransactions = new ArrayList<>();

        for (Transaction t : getAllTransaction()) {
            if (t.getStatus().toLowerCase().trim().equals("success") && t.getVoidedAt().trim().isEmpty() && t.getScheme().toLowerCase().trim().equals(schemeCode.toLowerCase().trim())) {
                schemeTransactions.add(t);
            }
        }

        return schemeTransactions;
    }

    public List<Transaction> getAllNoResponseTransaction() {
        List<Transaction> noResponseTransactions = new ArrayList<>();

        for (Transaction t : getAllTransaction()) {
            if (t.getStatus().trim().isEmpty()) {
                noResponseTransactions.add(t);
            }
        }

        return noResponseTransactions;
    }

    public void updateTransactionEntry(final Transaction transaction) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.updateDbTransactionEntry(transaction);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    public void deleteAllTransactionByBatchNo(final String batchNo) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.deleteDbAllTransactionByBatchNo(batchNo);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }

    public void deleteAllTransaction() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbDao.deleteDbAllTransaction();
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
        }
    }
}
