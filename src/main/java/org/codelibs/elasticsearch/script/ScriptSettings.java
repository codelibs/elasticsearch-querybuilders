/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codelibs.elasticsearch.script;

import org.codelibs.elasticsearch.common.settings.Setting;
import org.codelibs.elasticsearch.common.settings.Setting.Property;
import org.codelibs.elasticsearch.common.settings.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptSettings {

    static final String LEGACY_DEFAULT_LANG = "groovy";

    /**
     * The default script language to use for scripts that are stored in documents that have no script lang set explicitly.
     * This setting is legacy setting and only applies for indices created on ES versions prior to version 5.0
     *
     * This constant will be removed in the next major release.
     */
    @Deprecated
    public static final String LEGACY_SCRIPT_SETTING = "script.legacy.default_lang";

    private static final Map<ScriptType, Setting<Boolean>> SCRIPT_TYPE_SETTING_MAP;

    static {
        Map<ScriptType, Setting<Boolean>> scriptTypeSettingMap = new HashMap<>();
        for (ScriptType scriptType : ScriptType.values()) {
            scriptTypeSettingMap.put(scriptType, Setting.boolSetting(
                ScriptModes.sourceKey(scriptType),
                scriptType.isDefaultEnabled(),
                Property.NodeScope));
        }
        SCRIPT_TYPE_SETTING_MAP = Collections.unmodifiableMap(scriptTypeSettingMap);
    }

    private final Map<ScriptContext, Setting<Boolean>> scriptContextSettingMap;
    private final List<Setting<Boolean>> scriptLanguageSettings;
    private final Setting<String> defaultLegacyScriptLanguageSetting;

    public ScriptSettings(ScriptEngineRegistry scriptEngineRegistry, ScriptContextRegistry scriptContextRegistry) {
        Map<ScriptContext, Setting<Boolean>> scriptContextSettingMap = contextSettings(scriptContextRegistry);
        this.scriptContextSettingMap = Collections.unmodifiableMap(scriptContextSettingMap);

        List<Setting<Boolean>> scriptLanguageSettings = languageSettings(SCRIPT_TYPE_SETTING_MAP, scriptContextSettingMap, scriptEngineRegistry, scriptContextRegistry);
        this.scriptLanguageSettings = Collections.unmodifiableList(scriptLanguageSettings);

        this.defaultLegacyScriptLanguageSetting = new Setting<>(LEGACY_SCRIPT_SETTING, LEGACY_DEFAULT_LANG, setting -> {
            if (!LEGACY_DEFAULT_LANG.equals(setting) && !scriptEngineRegistry.getRegisteredLanguages().containsKey(setting)) {
                throw new IllegalArgumentException("unregistered default language [" + setting + "]");
            }
            return setting;
        }, Property.NodeScope);
    }

    private static Map<ScriptContext, Setting<Boolean>> contextSettings(ScriptContextRegistry scriptContextRegistry) {
        Map<ScriptContext, Setting<Boolean>> scriptContextSettingMap = new HashMap<>();
        for (ScriptContext scriptContext : scriptContextRegistry.scriptContexts()) {
            scriptContextSettingMap.put(scriptContext,
                    Setting.boolSetting(ScriptModes.operationKey(scriptContext), false, Property.NodeScope));
        }
        return scriptContextSettingMap;
    }

    private static List<Setting<Boolean>> languageSettings(Map<ScriptType, Setting<Boolean>> scriptTypeSettingMap,
                                                              Map<ScriptContext, Setting<Boolean>> scriptContextSettingMap,
                                                              ScriptEngineRegistry scriptEngineRegistry,
                                                              ScriptContextRegistry scriptContextRegistry) {
       throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    public List<Setting<?>> getSettings() {
        List<Setting<?>> settings = new ArrayList<>();
        settings.addAll(SCRIPT_TYPE_SETTING_MAP.values());
        settings.addAll(scriptContextSettingMap.values());
        settings.addAll(scriptLanguageSettings);
        settings.add(defaultLegacyScriptLanguageSetting);
        return settings;
    }

    public Iterable<Setting<Boolean>> getScriptLanguageSettings() {
        return scriptLanguageSettings;
    }

    public Setting<String> getDefaultLegacyScriptLanguageSetting() {
        return defaultLegacyScriptLanguageSetting;
    }

    public static String getLegacyDefaultLang(Settings settings) {
        return settings.get(LEGACY_SCRIPT_SETTING, ScriptSettings.LEGACY_DEFAULT_LANG);
    }
}
