/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.vfs2.provider.ftps;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;

/**
 * Create a FtpClient instance.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 * @since 2.0
 */
public final class FtpsClientFactory
{
    private FtpsClientFactory()
    {
    }

    /**
     * Creates a new connection to the server.
     * @param hostname The host name.
     * @param port The port.
     * @param username The user name for authentication.
     * @param password The user's password.
     * @param workingDirectory The directory to use.
     * @param fileSystemOptions The FileSystemOptions.
     * @return The FTPSClient.
     * @throws FileSystemException if an error occurs.
     */
    public static FTPSClient createConnection(String hostname, int port, char[] username, char[] password,
                                              String workingDirectory, FileSystemOptions fileSystemOptions)
        throws FileSystemException
    {
        // Determine the username and password to use
        if (username == null)
        {
            username = "anonymous".toCharArray();
        }

        if (password == null)
        {
            password = "anonymous".toCharArray();
        }

        try
        {

            final FTPSClientForCeligoFTPBridge clientForFTPBridge;

            if (FtpsFileSystemConfigBuilder.getInstance().getFtpsType(fileSystemOptions).equals("explicit"))
            {
                clientForFTPBridge = new FTPSClientForCeligoFTPBridge();
            }
            else if (FtpsFileSystemConfigBuilder.getInstance().getFtpsType(fileSystemOptions).equals("implicit"))
            {
                clientForFTPBridge = new FTPSClientForCeligoFTPBridge(true);
            }
            else
            {
                throw new FileSystemException(
                    "Invalid FTPS type of " + FtpsFileSystemConfigBuilder.getInstance().getFtpsType(
                        fileSystemOptions) + " specified. Must be 'implicit' or 'explicit'");
            }
            
            // Set only TLS protocols
            clientForFTPBridge.setEnabledProtocols(new String[]{ "TLSv1.2", "TLSv1.1", "TLSv1" });
            Boolean requireSocketReUse = FtpsFileSystemConfigBuilder.getInstance().getRequireSocketReUse(fileSystemOptions);
            if (requireSocketReUse != null)
            {
            	clientForFTPBridge.setRequireSocketReuse(requireSocketReUse.booleanValue());
            }

            String key = FtpsFileSystemConfigBuilder.getInstance().getEntryParser(fileSystemOptions);
            if (key != null)
            {
                FTPClientConfig config = new FTPClientConfig(key);

                String serverLanguageCode = FtpsFileSystemConfigBuilder.getInstance().getServerLanguageCode(
                    fileSystemOptions);
                if (serverLanguageCode != null)
                {
                    config.setServerLanguageCode(serverLanguageCode);
                }
                String defaultDateFormat = FtpsFileSystemConfigBuilder.getInstance().getDefaultDateFormat(
                    fileSystemOptions);
                if (defaultDateFormat != null)
                {
                    config.setDefaultDateFormatStr(defaultDateFormat);
                }
                String recentDateFormat = FtpsFileSystemConfigBuilder.getInstance().getRecentDateFormat(
                    fileSystemOptions);
                if (recentDateFormat != null)
                {
                    config.setRecentDateFormatStr(recentDateFormat);
                }
                String serverTimeZoneId = FtpsFileSystemConfigBuilder.getInstance().getServerTimeZoneId(
                    fileSystemOptions);
                if (serverTimeZoneId != null)
                {
                    config.setServerTimeZoneId(serverTimeZoneId);
                }
                String[] shortMonthNames = FtpsFileSystemConfigBuilder.getInstance().getShortMonthNames(
                    fileSystemOptions);
                if (shortMonthNames != null)
                {
                    StringBuilder shortMonthNamesStr = new StringBuilder(40);
                    for (int i = 0; i < shortMonthNames.length; i++)
                    {
                        if (shortMonthNamesStr.length() > 0)
                        {
                            shortMonthNamesStr.append("|");
                        }
                        shortMonthNamesStr.append(shortMonthNames[i]);
                    }
                    config.setShortMonthNames(shortMonthNamesStr.toString());
                }

                clientForFTPBridge.configure(config);
            }

            FTPFileEntryParserFactory myFactory = FtpsFileSystemConfigBuilder.getInstance().getEntryParserFactory(
                fileSystemOptions);
            if (myFactory != null)
            {
                clientForFTPBridge.setParserFactory(myFactory);
            }

            try
            {
                clientForFTPBridge.connect(hostname, port);

                int reply = clientForFTPBridge.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply))
                {
                    throw new FileSystemException("vfs.provider.ftp/connect-rejected.error", hostname);
                }

                // Login
                if (!clientForFTPBridge.login(
                    UserAuthenticatorUtils.toString(username),
                    UserAuthenticatorUtils.toString(password)))
                {
                    throw new FileSystemException("vfs.provider.ftp/login.error",
                        new Object[]{hostname, UserAuthenticatorUtils.toString(username)}, null);
                }

                // Set binary mode
                if (!clientForFTPBridge.setFileType(FTP.BINARY_FILE_TYPE))
                {
                    throw new FileSystemException("vfs.provider.ftp/set-binary.error", hostname);
                }

                // Set dataTimeout value
                Integer dataTimeout = FtpsFileSystemConfigBuilder.getInstance().getDataTimeout(fileSystemOptions);
                if (dataTimeout != null)
                {
                    clientForFTPBridge.setDataTimeout(dataTimeout.intValue());
                }

                // Change to root by default
                // All file operations a relative to the filesystem-root
                // String root = getRoot().getName().getPath();

                Boolean userDirIsRoot = FtpsFileSystemConfigBuilder.getInstance().getUserDirIsRoot(
                    fileSystemOptions);
                if (workingDirectory != null && (userDirIsRoot == null || !userDirIsRoot.booleanValue()))
                {
                    if (!clientForFTPBridge.changeWorkingDirectory(workingDirectory))
                    {
                        throw new FileSystemException("vfs.provider.ftp/change-work-directory.error",
                            workingDirectory);
                    }
                }

                Boolean passiveMode = FtpsFileSystemConfigBuilder.getInstance().getPassiveMode(fileSystemOptions);
                if (passiveMode != null && passiveMode.booleanValue())
                {
                    clientForFTPBridge.enterLocalPassiveMode();
                } else {
                	String activeExternalIPAddress = FtpsFileSystemConfigBuilder.getInstance().getActiveExternalIPAddress(fileSystemOptions);
                	if(activeExternalIPAddress != null )
                	{
                		clientForFTPBridge.setActiveExternalIPAddress(activeExternalIPAddress);
                	}
                	
                	String reportActiveExternalIPAddress = FtpsFileSystemConfigBuilder.getInstance().getReportActiveExternalIPAddress(fileSystemOptions);
                	if(reportActiveExternalIPAddress != null )
                	{
                		clientForFTPBridge.setReportActiveExternalIPAddress(reportActiveExternalIPAddress);
                	}
                	
                	Integer activePortRangeMin = FtpsFileSystemConfigBuilder.getInstance().getActivePortRangeMin(fileSystemOptions);
                	Integer activePortRangeMax = FtpsFileSystemConfigBuilder.getInstance().getActivePortRangeMax(fileSystemOptions);
                	
                	if(activePortRangeMin != null && activePortRangeMax != null && activePortRangeMin.compareTo(activePortRangeMax) <= 0)
                	{
                		clientForFTPBridge.setActivePortRange(activePortRangeMin.intValue(), activePortRangeMax.intValue());
                	}
                }
                
                // '0' means streaming, that's what we do!
                clientForFTPBridge.execPBSZ(0);
                clientForFTPBridge.execPROT(FtpsFileSystemConfigBuilder.getInstance().getDataChannelProtectionLevel(fileSystemOptions));
            }
            catch (final IOException e)
            {
                if (clientForFTPBridge.isConnected())
                {
                    clientForFTPBridge.disconnect();
                }
                throw e;
            }

            return clientForFTPBridge;
        }
        catch (final Exception exc)
        {
            throw new FileSystemException("vfs.provider.ftp/connect.error", new Object[]{hostname}, exc);
        }
    }
}
