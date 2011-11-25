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
class AddressExcelImporter extends AbstractExcelImporter {
    
    static Map CONFIG_ADDRESS_COLUMN_MAP = [
        sheet: 'Sheet1',
                        startRow: 1,
              columnMap: [
                'G':'houseNumber',
                'H':'street',
                'Y':'municipality'
              ]
                        
    ]
    
    static Map propertyConfigurationMap = [
        houseNumber: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null] ),
        street: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null]),
        municipality: ([expectedType: ExpectedPropertyType.StringType, defaultValue:'Unknown'])
    ]
    
    //List bookParamsList = ExcelImportUtils.columns(workbook, CONFIG_BOOK_COLUMN_MAP)
    
    //bookParamsList.each { Map bookParamMap ->
       // new Book (bookParamMap).save()
        
    //}
    public AddressExcelImporter(fileName3) {
        super(fileName3)
    }
    
    List<Map> getAddresses() {
        List AddressList = ExcelImportUtils.convertColumnMapConfigManyRows(workbook, CONFIG_ADDRESS_COLUMN_MAP, null, null, propertyConfigurationMap)
   // List bookList = ExcelImportUtils.convertColumnMapConfigManyRows(workbook, CONFIG_BOOK_COLUMN_MAP)
        return AddressList
    }

	
}
