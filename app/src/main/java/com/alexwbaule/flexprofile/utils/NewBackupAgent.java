package com.alexwbaule.flexprofile.utils;


import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.alexwbaule.flexprofile.BuildConfig;
import com.alexwbaule.flexprofile.MeuCarroApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;


/**
 * Created by alex on 29/04/16.
 */
public class NewBackupAgent extends BackupAgent {
    MeuCarroApplication carroApplication;
    private static String MY_BACKUP_KEY = "meucarrobackup";
    static final int BACKUP_AGENT_VERSION = 0;

    class backupState {
        long savedFileSize = -1;
        long savedCrc = -1;
        int savedVersion = -1;
    }

    class backupStuffs {
        byte[] bytes;
        long crc;
        long size;
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        carroApplication = MeuCarroApplication.getInstance();
        List<String> export = carroApplication.makeBackup();
        backupState state = readBackupState(oldState);
        backupStuffs backup = translateBackup(export);

        Log.d(getClass().getSimpleName(), "##### BACKUP CALL ########");

        if ((state.savedCrc != backup.crc || state.savedFileSize != backup.size) && state.savedVersion != BACKUP_AGENT_VERSION) {
            try {
                data.writeEntityHeader(MY_BACKUP_KEY, backup.bytes.length);
                data.writeEntityData(backup.bytes, backup.bytes.length);
                Log.d(getClass().getSimpleName(), String.format("##### BACKUP SAVED OK ((%d != %d || %d != %d) && %d != %d) ########",
                        state.savedCrc, backup.crc, state.savedFileSize, backup.size, state.savedVersion, BACKUP_AGENT_VERSION));
                writeBackupState(backup.size, backup.crc, newState);
            } catch (Exception e) {
                Log.d(getClass().getSimpleName(), "##### BACKUP FAILED ########");
            }
        } else {
            Log.d(getClass().getSimpleName(), String.format("##### BACKUP WAS NO MODIFICATION ((%d != %d || %d != %d) && %d != %d) ########",
                    state.savedCrc, backup.crc, state.savedFileSize, backup.size, state.savedVersion, BACKUP_AGENT_VERSION));
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        Log.d(getClass().getSimpleName(), String.format("##### RESTORE CALL ########  AppVersion --> %d", appVersionCode));

        if (appVersionCode == BuildConfig.VERSION_CODE) {
            while (data.readNextHeader()) {
                String key = data.getKey();
                int dataSize = data.getDataSize();

                if (key.equals(MY_BACKUP_KEY)) {
                    // process this kind of record here
                    byte[] buffer = new byte[dataSize];
                    data.readEntityData(buffer, 0, dataSize); // reads the entire entity at once
                    processRestore(buffer, newState);
                }
            }
        }

    }

    protected void processRestore(byte[] b, ParcelFileDescriptor newState) {
        carroApplication = MeuCarroApplication.getInstance();
        List<String> files = new ArrayList<>();
        CRC32 crc = new CRC32();


        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream in = new DataInputStream(bais);
        try {
            crc.update(b);
            while (in.available() > 0) {
                String element = in.readUTF();
                files.add(element);
            }

        } catch (IOException e) {
            Log.d(getClass().getSimpleName(), "##### RESTORE ERROR BUILD LIST ########");
        }
        if (!files.isEmpty()) {
            try {
                carroApplication.makeRestore(files);
                writeBackupState(files.size(), crc.getValue(), newState);
            } catch (IOException e) {
                Log.d(getClass().getSimpleName(), "##### RESTORE ERROR writeBackupState ########");
            }
        } else {
            Log.d(getClass().getSimpleName(), "##### RESTORE ERROR EMPTY LIST ########");
        }
    }

    private backupState readBackupState(ParcelFileDescriptor oldState) {
        backupState bkpstd = new backupState();
        DataInputStream in = new DataInputStream(new FileInputStream(oldState.getFileDescriptor()));
        try {
            bkpstd.savedFileSize = in.readLong();
            bkpstd.savedCrc = in.readLong();
            bkpstd.savedVersion = in.readInt();
        } catch (IOException e) {
            // It means we had no previous state; that's fine
        }
        return bkpstd;
    }

    // Write the given metrics to the new state file
    private void writeBackupState(long fileSize, long crc, ParcelFileDescriptor stateFile) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(stateFile.getFileDescriptor()));
        out.writeLong(fileSize);
        out.writeLong(crc);
        out.writeInt(BACKUP_AGENT_VERSION);
        Log.d(getClass().getSimpleName(), "##### BACKUP STATE WRITED ######## size(" + fileSize + ") crc (" + crc + ")");
    }

    private backupStuffs translateBackup(List<String> export) throws IOException {
        CRC32 crc = new CRC32();
        backupStuffs bkp = new backupStuffs();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (String element : export) {
            out.writeUTF(element);
        }
        byte[] bytes = baos.toByteArray();
        crc.update(bytes);
        bkp.crc = crc.getValue();
        bkp.bytes = bytes;
        bkp.size = export.size();

        return bkp;
    }

}
