package bz.voter.management.zk

import org.zkoss.zk.ui.*
import org.zkoss.zul.*

import java.lang.reflect.Field


class ComposerHelper {

	 static initializeListbox(Listbox listbox, Object objectInstance, String field){
	 	listbox.getChildren().clear()

		listbox.append{
			for(item in objectInstance.class.getDeclaredField(field).type.list()){
				listitem(value: item, selected:false){
					listcell(label: item.toString())
					listcell(label: item.id)
				}
			}
		}

		if(objectInstance){
			Field privateField = objectInstance.class.getDeclaredField(field)
			privateField.setAccessible(true)

			for(item in listbox.getItems()){
				if(item.getValue()?.id == privateField.get(objectInstance)?.id){
					listbox.setSelectedItem(item)
				}
			}
		}

	 }

	 static permissionDeniedBox(){
	 	Messagebox.show('Access Denied!', 'Access!', Messagebox.OK,
			Messagebox.ERROR)
	 }

}
