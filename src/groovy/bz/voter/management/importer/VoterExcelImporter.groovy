/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bz.voter.management.importer

import org.grails.plugins.excelimport.ExcelImportUtils
import org.grails.plugins.excelimport.*
import bz.voter.management.*

/**
 *
 * @author BOLGU
 */
class VoterExcelImporter extends AbstractExcelImporter {
    
    static Map CONFIG_VOTER_COLUMN_MAP = [
        sheet: 'Sheet1',
                        startRow: 1,
              columnMap: [
                'X':'Person.id',
                'B':'registrationDate',
                'A':'registrationNumber',
                'T':'Affiliation.id',
                'Q':'IdentificationType.id'
                 ]
                        
    ]
    
    static Map propertyConfigurationMap = [
         registrationDate: ([expectedType: ExpectedPropertyType.DateJavaType, defaultValue:null]),
         registrationNumber: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null]),
         person: ([expectedType: ExpectedPropertyType.IntType, defaultValue:null]),
        identificationType: ([expectedType: ExpectedPropertyType.IntType, defaultValue:null]),
         affiliation: ([expectedType: ExpectedPropertyType.IntType, defaultValue:null])
    ]
    

    public VoterExcelImporter(fileName3) {
        super(fileName3)
    }
    
    List<Map> getVoters() {
        List VoterList = ExcelImportUtils.convertColumnMapConfigManyRows(workbook, CONFIG_VOTER_COLUMN_MAP, null, null, propertyConfigurationMap)
   // List bookList = ExcelImportUtils.convertColumnMapConfigManyRows(workbook, CONFIG_BOOK_COLUMN_MAP)
        return VoterList
    }
}
