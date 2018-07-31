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

import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystem;

/**
 * The config BUILDER for various ftp configuration options.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 * @since 2.0
 */
public final class FtpsFileSystemConfigBuilder extends FileSystemConfigBuilder
{
    private static final FtpsFileSystemConfigBuilder BUILDER =
        new FtpsFileSystemConfigBuilder();

    private static final String FACTORY_KEY =
        FTPFileEntryParserFactory.class.getName() + ".KEY";
    private static final String PASSIVE_MODE =
        FtpsFileSystemConfigBuilder.class.getName() + ".PASSIVE";
    private static final String USER_DIR_IS_ROOT =
        FtpsFileSystemConfigBuilder.class.getName() + ".USER_DIR_IS_ROOT";
    private static final String DATA_TIMEOUT =
        FtpsFileSystemConfigBuilder.class.getName() + ".DATA_TIMEOUT";
    private static final String FTPS_TYPE =
        FtpsFileSystemConfigBuilder.class.getName() + ".FTPS_TYPE";
    private static final String PROT =
        FtpsFileSystemConfigBuilder.class.getName() + ".PROT";
    private static final String REQUIRE_SOCKET_REUSE =
    	FtpsFileSystemConfigBuilder.class.getName() + ".REQUIRE_SOCKET_REUSE";

	private static final String SERVER_LANGUAGE_CODE =
        FtpsFileSystemConfigBuilder.class.getName() + ".SERVER_LANGUAGE_CODE";
    private static final String DEFAULT_DATE_FORMAT =
        FtpsFileSystemConfigBuilder.class.getName() + ".DEFAULT_DATE_FORMAT";
    private static final String RECENT_DATE_FORMAT =
        FtpsFileSystemConfigBuilder.class.getName() + ".RECENT_DATE_FORMAT";
    private static final String SERVER_TIME_ZONE_ID =
        FtpsFileSystemConfigBuilder.class.getName() + ".SERVER_TIME_ZONE_ID";
    private static final String SHORT_MONTH_NAMES =
        FtpsFileSystemConfigBuilder.class.getName() + ".SHORT_MONTH_NAMES";
    
    private static final String ACTIVE_EXTERNAL_IP_ADDRESS = 
    	FtpsFileSystemConfigBuilder.class.getName() + ".ACTIVE_EXTERNAL_IP_ADDRESS";
    private static final String REPORT_ACTIVE_EXTERNAL_IP_ADDRESS =
    	FtpsFileSystemConfigBuilder.class.getName() + ".REPORT_ACTIVE_EXTERNAL_IP_ADDRESS";
    private static final String ACTIVE_PORT_RANGE_MIN =
    	FtpsFileSystemConfigBuilder.class.getName() + ".ACTIVE_PORT_RANGE_MIN";
    private static final String ACTIVE_PORT_RANGE_MAX =
    	FtpsFileSystemConfigBuilder.class.getName() + ".ACTIVE_PORT_RANGE_MAX";

    private FtpsFileSystemConfigBuilder()
    {
    }

    public static FtpsFileSystemConfigBuilder getInstance()
    {
        return BUILDER;
    }

    /**
     * FTPFileEntryParserFactory which will be used for ftp-entry parsing.
     *
     * @param opts The FileSystemOptions.
     * @param factory instance of your factory
     */
    public void setEntryParserFactory(FileSystemOptions opts, FTPFileEntryParserFactory factory)
    {
        setParam(opts, FTPFileEntryParserFactory.class.getName(), factory);
    }

    /**
     * @param opts The FileSystemOptions
     * @return The FTPFileEntryParserFactory.
     * @see #setEntryParserFactory
     */
    public FTPFileEntryParserFactory getEntryParserFactory(FileSystemOptions opts)
    {
        return (FTPFileEntryParserFactory) getParam(opts, FTPFileEntryParserFactory.class.getName());
    }

    /**
     * set the FQCN of your FileEntryParser used to parse the directory listing from your server.<br />
     * <br />
     * <i>If you do not use the default commons-net FTPFileEntryParserFactory e.g. by using
     * {@link #setEntryParserFactory}
     * this is the "key" parameter passed as argument into your custom factory</i>
     *
     * @param opts The FileSystemOptions.
     * @param key The key.
     */
    public void setEntryParser(FileSystemOptions opts, String key)
    {
        setParam(opts, FACTORY_KEY, key);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return The key.
     * @see #setEntryParser
     */
    public String getEntryParser(FileSystemOptions opts)
    {
        return (String) getParam(opts, FACTORY_KEY);
    }

    @Override
    protected Class<? extends FileSystem> getConfigClass()
    {
        return FtpFileSystem.class;
    }

    /**
     * Enter into passive mode.
     *
     * @param opts The FileSystemOptions.
     * @param passiveMode true if passive mode should be used, false otherwise.
     */
    public void setPassiveMode(FileSystemOptions opts, boolean passiveMode)
    {
        setParam(opts, PASSIVE_MODE, passiveMode ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return true if passive mode is being used.
     * @see #setPassiveMode
     */
    public Boolean getPassiveMode(FileSystemOptions opts)
    {
        return (Boolean) getParam(opts, PASSIVE_MODE);
    }

    /**
     * use user directory as root (do not change to fs root).
     *
     * @param opts The FileSystemOptions.
     * @param userDirIsRoot true if the user directory should be the root.
     */
    public void setUserDirIsRoot(FileSystemOptions opts, boolean userDirIsRoot)
    {
        setParam(opts, USER_DIR_IS_ROOT,
            userDirIsRoot ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return true if the user directory is the root.
     * @see #setUserDirIsRoot
     */
    public Boolean getUserDirIsRoot(FileSystemOptions opts)
    {
        return getBoolean(opts, USER_DIR_IS_ROOT, Boolean.TRUE);
    }

    /**
     * Set FTPS security mode, either "implicit" or "explicit".
     *
     * @param opts The FileSystemOptions.
     * @param ftpsType The file type.
     */
    public void setFtpsType(FileSystemOptions opts, String ftpsType)
    {
        setParam(opts, FTPS_TYPE, ftpsType);
    }

    /**
     * Return the FTPS security mode. Defaults to "explicit" if not defined.
     *
     * @param opts The FileSystemOptions.
     * @return The file type.
     * @see #setFtpsType
     */
    public String getFtpsType(FileSystemOptions opts)
    {
        return getString(opts, FTPS_TYPE, "explicit");
    }
    
    /**
     * Return the data channel protection level (PROT). Defaults to "P" if not defined
     *
     * @param opts The FileSystemOptions.
     * @return The PROT value.
     * @see org.apache.commons.net.ftp.FTPSClient#execPROT(String)
     * ported from 2.1
     */
    public String getDataChannelProtectionLevel(final FileSystemOptions opts)
    {
        return getString(opts, PROT, "P");
    }

    /**
     * @param opts The FileSystemOptions.
     * @param prot The PROT value
     * @see org.apache.commons.net.ftp.FTPSClient#execPROT(String)
     * ported from 2.1
     */
    public void setDataChannelProtectionLevel(final FileSystemOptions opts, String prot)
    {
        setParam(opts, PROT, prot);
    }
    
    /**
     * @param opts The FileSystemOptions.
     * @return true if TLS socket is reused (FTPS control and data ports are reused)
     * @see #setRequireSocketReUse
     */
    public Boolean getRequireSocketReUse(final FileSystemOptions opts)
    {
    	return (Boolean) getParam(opts, REQUIRE_SOCKET_REUSE);
    }
    
    /**
     * @param opts The FileSystemOptions.
     * @param requireSocketReuse true if the TLS socket must be reused (FTPS control and data ports are reused)
     */
    public void setRequireSocketReUse(final FileSystemOptions opts, boolean requireSocketReuse)
    {
    	setParam(opts, REQUIRE_SOCKET_REUSE,
			requireSocketReuse ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return The timeout value.
     * @see #setDataTimeout
     */
    public Integer getDataTimeout(FileSystemOptions opts)
    {
        return (Integer) getParam(opts, DATA_TIMEOUT);
    }

    /**
     * set the data timeout for the ftp client.<br />
     * If you set the dataTimeout to <code>null</code> no dataTimeout will be set on the
     * ftp client.
     *
     * @param opts  The FileSystemOptions.
     * @param dataTimeout The timeout value.
     */
    public void setDataTimeout(FileSystemOptions opts, Integer dataTimeout)
    {
        setParam(opts, DATA_TIMEOUT, dataTimeout);
    }

    /**
     * get the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions.
     * @return The language code.
     */
    public String getServerLanguageCode(FileSystemOptions opts)
    {
        return (String) getParam(opts, SERVER_LANGUAGE_CODE);
    }

    /**
     * set the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions.
     * @param serverLanguageCode the language code.
     */
    public void setServerLanguageCode(FileSystemOptions opts,
                                      String serverLanguageCode)
    {
        setParam(opts, SERVER_LANGUAGE_CODE, serverLanguageCode);
    }

    /**
     * get the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions.
     * @return The default date format.
     */
    public String getDefaultDateFormat(FileSystemOptions opts)
    {
        return (String) getParam(opts, DEFAULT_DATE_FORMAT);
    }

    /**
     * set the language code used by the server. see {@link org.apache.commons.net.ftp.FTPClientConfig}
     * for details and examples.
     * @param opts The FileSystemOptions.
     * @param defaultDateFormat The default date format.
     */
    public void setDefaultDateFormat(FileSystemOptions opts,
                                     String defaultDateFormat)
    {
        setParam(opts, DEFAULT_DATE_FORMAT, defaultDateFormat);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @return The recent date format.
     */
    public String getRecentDateFormat(FileSystemOptions opts)
    {
        return (String) getParam(opts, RECENT_DATE_FORMAT);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions
     * @param recentDateFormat The recent date format.
     */
    public void setRecentDateFormat(FileSystemOptions opts,
                                    String recentDateFormat)
    {
        setParam(opts, RECENT_DATE_FORMAT, recentDateFormat);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @return The server timezone id.
     */
    public String getServerTimeZoneId(FileSystemOptions opts)
    {
        return (String) getParam(opts, SERVER_TIME_ZONE_ID);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @param serverTimeZoneId The server's timezone id.
     */
    public void setServerTimeZoneId(FileSystemOptions opts,
                                    String serverTimeZoneId)
    {
        setParam(opts, SERVER_TIME_ZONE_ID, serverTimeZoneId);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @return An array of short month names.
     */
    public String[] getShortMonthNames(FileSystemOptions opts)
    {
        return (String[]) getParam(opts, SHORT_MONTH_NAMES);
    }

    /**
     * see {@link org.apache.commons.net.ftp.FTPClientConfig} for details and examples.
     * @param opts The FileSystemOptions.
     * @param shortMonthNames An array of short month names.
     */
    public void setShortMonthNames(FileSystemOptions opts,
                                   String[] shortMonthNames)
    {
        String[] clone = null;
        if (shortMonthNames != null)
        {
            clone = new String[shortMonthNames.length];
            System.arraycopy(shortMonthNames, 0, clone, 0, shortMonthNames.length);
        }

        setParam(opts, SHORT_MONTH_NAMES, clone);
    }
    
    /**
     * @param opts The FileSystemOptions.
     * @return The External IP Address used by Active mode
     * @since 2.0.C
     * */
    public String getActiveExternalIPAddress(FileSystemOptions opts)
    {
        return  (String) getParam(opts, ACTIVE_EXTERNAL_IP_ADDRESS);
    }
    
    /**
     * @param opts The FileSystemOptions.
     * @param activeExternalIPAddress The External IP Address to use in Active mode
     * @since 2.0.C
     */
    public void setActiveExternalIPAddress(FileSystemOptions opts, String activeExternalIPAddress)
    {
        setParam(opts, ACTIVE_EXTERNAL_IP_ADDRESS, activeExternalIPAddress);
    }
    
    /**
     * @param opts The FileSystemOptions.
     * @return The External IP Address *reported* when in Active mode.
     * @since 2.0.C
     * */
    public String getReportActiveExternalIPAddress(FileSystemOptions opts)
    {
        return  (String) getParam(opts, REPORT_ACTIVE_EXTERNAL_IP_ADDRESS);
    }
    
    /**
     * see {@link org.apache.commons.net.ftp.FTP#setControlEncoding} for details and examples.
     * @param opts The FileSystemOptions.
     * @param reportActiveExternalIPAddress The External IP Address to *report* when in Active mode.
     * @since 2.0.C
     */
    public void setReportActiveExternalIPAddress(FileSystemOptions opts, String reportActiveExternalIPAddress)
    {
        setParam(opts, REPORT_ACTIVE_EXTERNAL_IP_ADDRESS, reportActiveExternalIPAddress);
    }
    
    /**
     * @param opts The FileSystem options.
     * @return The Port range's lower bound for Active mode 
     * @since 2.0.C
     */
    public Integer getActivePortRangeMin(FileSystemOptions opts)
    {
        return (Integer) getParam(opts, ACTIVE_PORT_RANGE_MIN);
    }

    /**
     * @param opts The FileSystem options.
     * @param activePortRangeMin The Port range's lower bound to use in Active mode
     * @since 2.0.C
     */
    public void setActivePortRangeMin(FileSystemOptions opts, Integer activePortRangeMin)
    {
        setParam(opts, ACTIVE_PORT_RANGE_MIN, activePortRangeMin);
    }
    
    /**
     * @param opts The FileSystem options.
     * @return The Port range's upper bound for Active mode 
     * @since 2.0.C
     */
    public Integer getActivePortRangeMax(FileSystemOptions opts)
    {
        return (Integer) getParam(opts, ACTIVE_PORT_RANGE_MAX);
    }

    /**
     * @param opts The FileSystem options.
     * @param activePortRangeMax The Port range's upper bound to use in Active mode
     * @since 2.0.C
     */
    public void setActivePortRangeMax(FileSystemOptions opts, Integer activePortRangeMax)
    {
        setParam(opts, ACTIVE_PORT_RANGE_MAX, activePortRangeMax);
    }
}
