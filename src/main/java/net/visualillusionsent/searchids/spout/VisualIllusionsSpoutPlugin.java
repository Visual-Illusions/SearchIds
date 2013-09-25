/*
 * This file is part of SearchIds.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * SearchIds is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SearchIds is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SearchIds.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.searchids.spout;

import net.visualillusionsent.utils.ProgramStatus;
import net.visualillusionsent.utils.VersionChecker;
import org.spout.api.plugin.Plugin;
import org.spout.cereal.config.yaml.YamlConfiguration;

import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Visual Illusions Spout Plugin extension
 *
 * @author Jason (darkdiplomat)
 */
public abstract class VisualIllusionsSpoutPlugin extends Plugin {

    private VersionChecker vc;
    private YamlConfiguration pluginyml;

    void initialize() {
        this.vc = new VersionChecker(getName(), getDescription().getVersion(), getBuild(), getVersionCheckURL(), getStatus(), false);
    }

    protected final void checkStatus() {
        String statusReport = "%s has decleared itself as '%s' build. %s";
        switch (this.getStatus()) {
            case UNKNOWN:
                getLogger().severe(String.format(statusReport, getName(), "UNKNOWN STATUS", "Use is not advised and could cause damage to your system!"));
                break;
            case ALPHA:
                getLogger().severe(String.format(statusReport, getName(), "ALPHA", "Production use is not advised!"));
                break;
            case BETA:
                getLogger().severe(String.format(statusReport, getName(), "BETA", "Production use is not advised!"));
                break;
            case RELEASE_CANDIDATE:
                getLogger().severe(String.format(statusReport, getName(), "RELEASE CANDIDATE", "Expect some bugs."));
                break;
        }
    }

    protected final void checkVersion() {
        Boolean islatest = vc.isLatest();
        if (islatest == null) {
            getLogger().warning("VersionCheckerError: " + vc.getErrorMessage());
        }
        else if (!islatest) {
            getLogger().warning(vc.getUpdateAvailibleMessage());
            getLogger().warning(String.format("You can view update info @ %s#ChangeLog", getWikiURL()));
        }
    }

    final ProgramStatus getStatus() {
        try {
            return ProgramStatus.valueOf(getPluginYML().getChild("program.status").getString().toUpperCase());
        }
        catch (Exception ex) {
            return ProgramStatus.UNKNOWN;
        }
    }

    final String getBuild() {
        return getPluginYML().getChild("build.number").getString("0");
    }

    final String getBuildTime() {
        return getPluginYML().getChild("build.time").getString("19700101-0000");
    }

    final VersionChecker getVersionChecker() {
        return vc;
    }

    final String getVersionCheckURL() {
        return getPluginYML().getChild("version.check.url").getString("missing.url");
    }

    final String getWikiURL() {
        return getPluginYML().getChild("website").getString("missing.url");
    }

    final String getIssuesURL() {
        return getPluginYML().getChild("issues.url").getString("missing.url");
    }

    final String getDevelopers() {
        return getPluginYML().getChild("developers").getString("missing.developers");
    }

    private YamlConfiguration getPluginYML() {
        if (pluginyml == null) {
            try {
                JarFile jfile = new JarFile(getJarPath());
                JarEntry pyml = jfile.getJarEntry("properties.yml");
                pluginyml = new YamlConfiguration(jfile.getInputStream(pyml));
                pluginyml.load();
            }
            catch (Exception ex) {
                getLogger().warning("Failed to read Visual Illusions Information from properties.yml");
            }
        }
        return this.pluginyml;
    }

    private String getJarPath() { // For when the jar isn't SearchIds.jar
        try {
            CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
            return codeSource.getLocation().toURI().getPath();
        }
        catch (URISyntaxException ex) {
        }
        return "plugins/SearchIds.jar";
    }
}
