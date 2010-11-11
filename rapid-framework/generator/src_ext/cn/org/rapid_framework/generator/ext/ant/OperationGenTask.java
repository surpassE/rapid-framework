package cn.org.rapid_framework.generator.ext.ant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.ext.config.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.ext.ibatis.model.TableConfig;
import cn.org.rapid_framework.generator.ext.ibatis.model.TableConfigSet;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.StringHelper;

public class OperationGenTask extends BaseGeneratorTask {
    private String tableConfigFiles; 
    private String tableSqlName;
    
    @Override
    protected List<Map> getGeneratorContexts() throws SQLException, Exception {
        TableConfigSet tableConfigSet = parseForTableConfigSet();
        if("*".equals(tableSqlName)) {
            List<Map> result = new ArrayList();
            for(TableConfig tableConfig : tableConfigSet.getTableConfigs()) {
                result.addAll(toMaps(tableConfig));
            }
            return result;
        }else {
            List<Map> result = toMaps(tableConfigSet.getBySqlName(tableSqlName));
            return result;
        }
    }

    private List<Map> toMaps(TableConfig tableConfig) throws SQLException, Exception {
        List<Map> result = new ArrayList();
        for(Sql sql : tableConfig.getSqls()) {
            Map operationMap = new HashMap();
            operationMap.putAll(BeanHelper.describe(sql));
            operationMap.put("sql", sql);
            operationMap.put("basepackage", tableConfig.getBasepackage());
            operationMap.put("basepackage_dir", tableConfig.getBasepackage_dir());
            result.add(operationMap);
        }
        return result;
    }

    private String[] getTableConfigFilesArray() {
        return StringHelper.tokenizeToStringArray(tableConfigFiles, ", \t\n\r\f");
    }

    public void setTableConfigFiles(String tableConfigFiles) {
        this.tableConfigFiles = tableConfigFiles;
    }
    
    public void setTableSqlName(String tableSqlName) {
        this.tableSqlName = tableSqlName;
    }

    private TableConfigSet parseForTableConfigSet() {
        TableConfigSet tableConfigSet = new TableConfigXmlBuilder().parseFromXML(getProject().getBaseDir(), Arrays.asList(getTableConfigFilesArray()));
        return tableConfigSet;
    }
    
}