/*
 * Copyright (C) 2010-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.library;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TextEditDialog extends Dialog{
	private Context myContext;
	private EditText myEditText;
	
	public TextEditDialog(Context context, String title, String okName, String cancelName) {
		super(context);
		myContext = context;
		setTitle(title);
		
	   	LinearLayout linearLayout = new LinearLayout(myContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        myEditText = new EditText(myContext);
        linearLayout.addView(myEditText);

        LinearLayout btnLayout = new LinearLayout(myContext);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setLayoutParams(new LinearLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
    			LayoutParams.FILL_PARENT, 1f));
        btnLayout.setGravity(Gravity.FILL_HORIZONTAL);
        
        Button ok = new Button(myContext);
        ok.setText(okName);
        ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				okAction();
			}
		});
        
        Button cancel = new Button(myContext);
        cancel.setText(cancelName);
        cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancelAction();
			}
		});

    	LinearLayout.LayoutParams llppp = new LinearLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
    			LayoutParams.FILL_PARENT, 0.5f);
        btnLayout.addView(ok, llppp);
        btnLayout.addView(cancel, llppp);
        linearLayout.addView(btnLayout);
        setContentView(linearLayout);
	}

	public void setText(String text){
		myEditText.setText(text);
	}
	
	public String getText(){
		return myEditText.getText().toString().trim();
	}
	
	protected void cancelAction(){
		cancel();
	}
	
	protected void okAction()  {
		dismiss();
	}
	
}

class ToastMaker{
	private static ZLResource myResource = ZLResource.resource("libraryView");

	public static void MakeToast(Context context, String messageKey){
		Toast.makeText(context,	myResource.getResource(messageKey).getValue(),
				Toast.LENGTH_SHORT).show();
	}
}

/*
class RenameDialog extends TextEditDialog{
	private ZLFile myFile;
	private Context myContext;
	
	private static ZLResource myResource = ZLResource.resource("libraryView");
	
	RenameDialog(Context context, ZLFile file) {
		super(context,
				myResource.getResource("rename").getValue(),
				ZLResource.resource("dialog").getResource("button").getResource("rename").getValue(),
				ZLResource.resource("dialog").getResource("button").getResource("cancel").getValue()
				);
		myContext = context;
		myFile = file;
		
		if(myFile.isDirectory()){
			setText(myFile.getShortName());
		}else{
			String extension = myFile.getExtension(); 
			String name = myFile.getShortName();
			name = name.substring(0, name.indexOf(extension) - 1);
			setText(name);
		}
	}

	protected void cancelAction(){
		cancel();
	}
	
	public void okAction()  {
		String newName = getText();
		if (newName == ""){
			dismiss();
			return;
		}
		if (!myFile.isDirectory())
			newName += "." + myFile.getExtension();
		if (newName.startsWith(".")){
			ToastMaker.MakeToast(myContext, "messFileIncorrect");
		} else if (!FileUtil.contain(newName, FileUtil.getParent(myFile))){
			if(myFile.getPhysicalFile().rename(newName)){
				((Activity) myContext).startActivityForResult(
						new Intent(myContext, FileManager.class)
							.putExtra(FileManager.FILE_MANAGER_PATH, FileUtil.getParent(myFile).getPath())
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
						FileManager.CHILD_LIST_REQUEST
				);
				dismiss();
			} else {
				ToastMaker.MakeToast(myContext, "messRenameFailed");
			}
		}else{
			ToastMaker.MakeToast(myContext, "messFileExists");
		}
	}
}
*/

class MkDirDialog extends TextEditDialog{
	private Context myContext;
	private String myPath;
	private String myInsertPath;
	
	private static ZLResource myResource = ZLResource.resource("libraryView");
	
	MkDirDialog(Context context, String curPath, String insertPath) {
		super(context,
				myResource.getResource("newDirectory").getValue(),
				ZLResource.resource("dialog").getResource("button").getResource("ok").getValue(),
				ZLResource.resource("dialog").getResource("button").getResource("cancel").getValue()
				);
		myContext = context;
		myPath = curPath;
		myInsertPath = insertPath;
		setText(myResource.getResource("newDirectory").getValue());
	}

	protected void cancelAction(){
		cancel();
	}
	
	public void okAction()  {
		String newName = getText();
		ZLFile file = ZLFile.createFileByPath(myPath);
		if (newName == ""){
			dismiss();
			return;
		}else if (!file.isDirectory()){
			ToastMaker.MakeToast(myContext, "messDirectoryIntoArchive");
			dismiss();
			return;
		}
			
		if (!FileUtil.contain(newName, file)){
			ZLFile.createFileByPath(myPath + "/" + newName).mkdir();
			FileManager.myInsertPathStatic = myInsertPath;
			((FileManager)myContext).refresh();
			dismiss();
		}else{
			ToastMaker.MakeToast(myContext, "messFileExists");
		}
	}
}

class RadioButtonDialog{
	protected Context myContext;
	private String myTitle;
	private String[] myItems;
	private int mySelectedItem;
	
	public RadioButtonDialog(Context context, String title, String[] items, int selectedItem){
		myContext = context;
		myTitle = title;
		myItems = items;
		mySelectedItem = selectedItem;
	}

	protected void itemSelected(DialogInterface dialog, int item){
		dialog.dismiss();
	}
	
	public void show(){
		AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
		builder.setTitle(myTitle);
		builder.setSingleChoiceItems(myItems, mySelectedItem, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	itemSelected(dialog, item);
		    }
		});
		builder.create().show();
	}
}

class SortingDialog extends RadioButtonDialog{
	private static ZLResource myResource = ZLResource.resource("libraryView").getResource("sortingBox");
	private static String myTitle = myResource.getResource("title").getValue();
	private static String[] myItems = SortType.toStringArray();
	private String myPath;

	private static String SORT_GROUP = "sortGroup";
	private static String SORT_OPTION_NAME = "sortOptionName";
	private static int SORT_DEF_VALUE = 0;
	private static ZLIntegerOption mySortOption = new ZLIntegerOption(SORT_GROUP, SORT_OPTION_NAME, SORT_DEF_VALUE);

	public SortingDialog(Context content, String path) {
		super(content, myTitle, myItems, mySortOption.getValue());
		myPath = path;
	}

	@Override
	protected void itemSelected(DialogInterface dialog, int item){
		super.itemSelected(dialog, item);
		if (getOprionSortType().ordinal() != item){
			mySortOption.setValue(item);
			FileManager.mySortType = SortType.values()[item];
			
			((Activity) myContext).startActivityForResult(
					new Intent(myContext, FileManager.class)
						.putExtra(FileManager.FILE_MANAGER_PATH, myPath)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
					FileManager.CHILD_LIST_REQUEST
			);
		}
	}
	
	public static SortType getOprionSortType(){
		return SortType.values()[mySortOption.getValue()];
	}

	enum SortType{
		BY_NAME{
			public String getName() {
				return myResource.getResource("byName").getValue();
			}
		},
		BY_DATE{
			public String getName() {
				return myResource.getResource("byDate").getValue();
			}
		};

		public abstract String getName();
		
		public static String[] toStringArray(){
			SortType[] sourse = values();
			String[] result = new String[sourse.length];
			for (int i = 0; i < sourse.length; i++){
				result[i] = sourse[i].getName();
			}
			return result;
		}
	}
}

class ViewChangeDialog extends RadioButtonDialog{
	private static ZLResource myResource = ZLResource.resource("libraryView").getResource("viewBox");
	private static String myTitle = myResource.getResource("title").getValue();
	private static String[] myItems = ViewType.toStringArray();
	private String myPath;

	private static String VIEW_GROUP = "viewGroup";
	private static String VIEW_OPTION_NAME = "viewOptionName";
	private static int VIEW_DEF_VALUE = 0;
	private static ZLIntegerOption myViewOption = new ZLIntegerOption(VIEW_GROUP, VIEW_OPTION_NAME, VIEW_DEF_VALUE);

	public ViewChangeDialog(Context content, String path) {
		super(content, myTitle, myItems, myViewOption.getValue());
		myPath = path;
	}

	@Override
	protected void itemSelected(DialogInterface dialog, int item){
		super.itemSelected(dialog, item);
		if (getOprionViewType().ordinal() != item){
			myViewOption.setValue(item);
			FileManager.myViewType = ViewType.values()[item];
			if (FileManager.myViewType == ViewType.SIMPLE){
					((Activity) myContext).startActivityForResult(new Intent(
							myContext, FileManager.class).putExtra(
							FileManager.FILE_MANAGER_PATH, myPath).addFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP),
							FileManager.CHILD_LIST_REQUEST);
			} else if (FileManager.myViewType == ViewType.SKETCH){
					((Activity) myContext).startActivityForResult(new Intent(
							myContext, SketchGalleryActivity.class).putExtra(
							FileManager.FILE_MANAGER_PATH, myPath).addFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP),
							FileManager.CHILD_LIST_REQUEST);
			}
		}
	}
	
	public static ViewType getOprionViewType(){
		return ViewType.values()[myViewOption.getValue()];
	}

	enum ViewType{
		SIMPLE{
			public String getName() {
				return myResource.getResource("simple").getValue();
			}
		},
		SKETCH{
			public String getName() {
				return myResource.getResource("sketch").getValue();
			}
		};

		public abstract String getName();
		
		public static String[] toStringArray(){
			ViewType[] sourse = values();
			String[] result = new String[sourse.length];
			for (int i = 0; i < sourse.length; i++){
				result[i] = sourse[i].getName();
			}
			return result;
		}
	}
}