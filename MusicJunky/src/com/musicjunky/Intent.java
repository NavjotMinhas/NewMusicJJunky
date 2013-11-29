/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.musicjunky;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * An intent is an abstract description of an operation to be performed.  It
 * can be used with {@link Context#startActivity(Intent) startActivity} to
 * launch an {@link android.app.Activity},
 * {@link android.content.Context#sendBroadcast(Intent) broadcastIntent} to
 * send it to any interested {@link BroadcastReceiver BroadcastReceiver} components,
 * and {@link android.content.Context#startService} or
 * {@link android.content.Context#bindService} to communicate with a
 * background {@link android.app.Service}.
 *
 * <p>An Intent provides a facility for performing late runtime binding between the code in
 * different applications. Its most significant use is in the launching of activities, where it
 * can be thought of as the glue between activities. It is basically a passive data structure
 * holding an abstract description of an action to be performed.</p>
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For information about how to create and resolve intents, read the
 * <a href="{@docRoot}guide/topics/intents/intents-filters.html">Intents and Intent Filters</a>
 * developer guide.</p>
 * </div>
 *
 * <a name="IntentStructure"></a>
 * <h3>Intent Structure</h3>
 * <p>The primary pieces of information in an intent are:</p>
 *
 * <ul>
 *   <li> <p><b>action</b> -- The general action to be performed, such as
 *     {@link #ACTION_VIEW}, {@link #ACTION_EDIT}, {@link #ACTION_MAIN},
 *     etc.</p>
 *   </li>
 *   <li> <p><b>data</b> -- The data to operate on, such as a person record
 *     in the contacts database, expressed as a {@link android.net.Uri}.</p>
 *   </li>
 * </ul>
 *
 *
 * <p>Some examples of action/data pairs are:</p>
 *
 * <ul>
 *   <li> <p><b>{@link #ACTION_VIEW} <i>content://contacts/people/1</i></b> -- Display
 *     information about the person whose identifier is "1".</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_DIAL} <i>content://contacts/people/1</i></b> -- Display
 *     the phone dialer with the person filled in.</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_VIEW} <i>tel:123</i></b> -- Display
 *     the phone dialer with the given number filled in.  Note how the
 *     VIEW action does what what is considered the most reasonable thing for
 *     a particular URI.</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_DIAL} <i>tel:123</i></b> -- Display
 *     the phone dialer with the given number filled in.</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_EDIT} <i>content://contacts/people/1</i></b> -- Edit
 *     information about the person whose identifier is "1".</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_VIEW} <i>content://contacts/people/</i></b> -- Display
 *     a list of people, which the user can browse through.  This example is a
 *     typical top-level entry into the Contacts application, showing you the
 *     list of people. Selecting a particular person to view would result in a
 *     new intent { <b>{@link #ACTION_VIEW} <i>content://contacts/N</i></b> }
 *     being used to start an activity to display that person.</p>
 *   </li>
 * </ul>
 *
 * <p>In addition to these primary attributes, there are a number of secondary
 * attributes that you can also include with an intent:</p>
 *
 * <ul>
 *     <li> <p><b>category</b> -- Gives additional information about the action
 *         to execute.  For example, {@link #CATEGORY_LAUNCHER} means it should
 *         appear in the Launcher as a top-level application, while
 *         {@link #CATEGORY_ALTERNATIVE} means it should be included in a list
 *         of alternative actions the user can perform on a piece of data.</p>
 *     <li> <p><b>type</b> -- Specifies an explicit type (a MIME type) of the
 *         intent data.  Normally the type is inferred from the data itself.
 *         By setting this attribute, you disable that evaluation and force
 *         an explicit type.</p>
 *     <li> <p><b>component</b> -- Specifies an explicit name of a component
 *         class to use for the intent.  Normally this is determined by looking
 *         at the other information in the intent (the action, data/type, and
 *         categories) and matching that with a component that can handle it.
 *         If this attribute is set then none of the evaluation is performed,
 *         and this component is used exactly as is.  By specifying this attribute,
 *         all of the other Intent attributes become optional.</p>
 *     <li> <p><b>extras</b> -- This is a {@link Bundle} of any additional information.
 *         This can be used to provide extended information to the component.
 *         For example, if we have a action to send an e-mail message, we could
 *         also include extra pieces of data here to supply a subject, body,
 *         etc.</p>
 * </ul>
 *
 * <p>Here are some examples of other operations you can specify as intents
 * using these additional parameters:</p>
 *
 * <ul>
 *   <li> <p><b>{@link #ACTION_MAIN} with category {@link #CATEGORY_HOME}</b> --
 *     Launch the home screen.</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_GET_CONTENT} with MIME type
 *     <i>{@link android.provider.Contacts.Phones#CONTENT_URI
 *     vnd.android.cursor.item/phone}</i></b>
 *     -- Display the list of people's phone numbers, allowing the user to
 *     browse through them and pick one and return it to the parent activity.</p>
 *   </li>
 *   <li> <p><b>{@link #ACTION_GET_CONTENT} with MIME type
 *     <i>*{@literal /}*</i> and category {@link #CATEGORY_OPENABLE}</b>
 *     -- Display all pickers for data that can be opened with
 *     {@link ContentResolver#openInputStream(Uri) ContentResolver.openInputStream()},
 *     allowing the user to pick one of them and then some data inside of it
 *     and returning the resulting URI to the caller.  This can be used,
 *     for example, in an e-mail application to allow the user to pick some
 *     data to include as an attachment.</p>
 *   </li>
 * </ul>
 *
 * <p>There are a variety of standard Intent action and category constants
 * defined in the Intent class, but applications can also define their own.
 * These strings use java style scoping, to ensure they are unique -- for
 * example, the standard {@link #ACTION_VIEW} is called
 * "android.intent.action.VIEW".</p>
 *
 * <p>Put together, the set of actions, data types, categories, and extra data
 * defines a language for the system allowing for the expression of phrases
 * such as "call john smith's cell".  As applications are added to the system,
 * they can extend this language by adding new actions, types, and categories, or
 * they can modify the behavior of existing phrases by supplying their own
 * activities that handle them.</p>
 *
 * <a name="IntentResolution"></a>
 * <h3>Intent Resolution</h3>
 *
 * <p>There are two primary forms of intents you will use.
 *
 * <ul>
 *     <li> <p><b>Explicit Intents</b> have specified a component (via
 *     {@link #setComponent} or {@link #setClass}), which provides the exact
 *     class to be run.  Often these will not include any other information,
 *     simply being a way for an application to launch various internal
 *     activities it has as the user interacts with the application.
 *
 *     <li> <p><b>Implicit Intents</b> have not specified a component;
 *     instead, they must include enough information for the system to
 *     determine which of the available components is best to run for that
 *     intent.
 * </ul>
 *
 * <p>When using implicit intents, given such an arbitrary intent we need to
 * know what to do with it. This is handled by the process of <em>Intent
 * resolution</em>, which maps an Intent to an {@link android.app.Activity},
 * {@link BroadcastReceiver}, or {@link android.app.Service} (or sometimes two or
 * more activities/receivers) that can handle it.</p>
 *
 * <p>The intent resolution mechanism basically revolves around matching an
 * Intent against all of the &lt;intent-filter&gt; descriptions in the
 * installed application packages.  (Plus, in the case of broadcasts, any {@link BroadcastReceiver}
 * objects explicitly registered with {@link Context#registerReceiver}.)  More
 * details on this can be found in the documentation on the {@link
 * IntentFilter} class.</p>
 *
 * <p>There are three pieces of information in the Intent that are used for
 * resolution: the action, type, and category.  Using this information, a query
 * is done on the {@link PackageManager} for a component that can handle the
 * intent. The appropriate component is determined based on the intent
 * information supplied in the <code>AndroidManifest.xml</code> file as
 * follows:</p>
 *
 * <ul>
 *     <li> <p>The <b>action</b>, if given, must be listed by the component as
 *         one it handles.</p>
 *     <li> <p>The <b>type</b> is retrieved from the Intent's data, if not
 *         already supplied in the Intent.  Like the action, if a type is
 *         included in the intent (either explicitly or implicitly in its
 *         data), then this must be listed by the component as one it handles.</p>
 *     <li> For data that is not a <code>content:</code> URI and where no explicit
 *         type is included in the Intent, instead the <b>scheme</b> of the
 *         intent data (such as <code>http:</code> or <code>mailto:</code>) is
 *         considered. Again like the action, if we are matching a scheme it
 *         must be listed by the component as one it can handle.
 *     <li> <p>The <b>categories</b>, if supplied, must <em>all</em> be listed
 *         by the activity as categories it handles.  That is, if you include
 *         the categories {@link #CATEGORY_LAUNCHER} and
 *         {@link #CATEGORY_ALTERNATIVE}, then you will only resolve to components
 *         with an intent that lists <em>both</em> of those categories.
 *         Activities will very often need to support the
 *         {@link #CATEGORY_DEFAULT} so that they can be found by
 *         {@link Context#startActivity Context.startActivity()}.</p>
 * </ul>
 *
 * <p>For example, consider the Note Pad sample application that
 * allows user to browse through a list of notes data and view details about
 * individual items.  Text in italics indicate places were you would replace a
 * name with one specific to your own package.</p>
 *
 * <pre> &lt;manifest xmlns:android="http://schemas.android.com/apk/res/android"
 *       package="<i>com.android.notepad</i>"&gt;
 *     &lt;application android:icon="@drawable/app_notes"
 *             android:label="@string/app_name"&gt;
 *
 *         &lt;provider class=".NotePadProvider"
 *                 android:authorities="<i>com.google.provider.NotePad</i>" /&gt;
 *
 *         &lt;activity class=".NotesList" android:label="@string/title_notes_list"&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.MAIN" /&gt;
 *                 &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
 *             &lt;/intent-filter&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.VIEW" /&gt;
 *                 &lt;action android:name="android.intent.action.EDIT" /&gt;
 *                 &lt;action android:name="android.intent.action.PICK" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.GET_CONTENT" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *         &lt;/activity&gt;
 *
 *         &lt;activity class=".NoteEditor" android:label="@string/title_note"&gt;
 *             &lt;intent-filter android:label="@string/resolve_edit"&gt;
 *                 &lt;action android:name="android.intent.action.VIEW" /&gt;
 *                 &lt;action android:name="android.intent.action.EDIT" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.INSERT" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *
 *         &lt;/activity&gt;
 *
 *         &lt;activity class=".TitleEditor" android:label="@string/title_edit_title"
 *                 android:theme="@android:style/Theme.Dialog"&gt;
 *             &lt;intent-filter android:label="@string/resolve_title"&gt;
 *                 &lt;action android:name="<i>com.android.notepad.action.EDIT_TITLE</i>" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;category android:name="android.intent.category.ALTERNATIVE" /&gt;
 *                 &lt;category android:name="android.intent.category.SELECTED_ALTERNATIVE" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *         &lt;/activity&gt;
 *
 *     &lt;/application&gt;
 * &lt;/manifest&gt;</pre>
 *
 * <p>The first activity,
 * <code>com.android.notepad.NotesList</code>, serves as our main
 * entry into the app.  It can do three things as described by its three intent
 * templates:
 * <ol>
 * <li><pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_MAIN android.intent.action.MAIN}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_LAUNCHER android.intent.category.LAUNCHER}" /&gt;
 * &lt;/intent-filter&gt;</pre>
 * <p>This provides a top-level entry into the NotePad application: the standard
 * MAIN action is a main entry point (not requiring any other information in
 * the Intent), and the LAUNCHER category says that this entry point should be
 * listed in the application launcher.</p>
 * <li><pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_VIEW android.intent.action.VIEW}" /&gt;
 *     &lt;action android:name="{@link #ACTION_EDIT android.intent.action.EDIT}" /&gt;
 *     &lt;action android:name="{@link #ACTION_PICK android.intent.action.PICK}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data mimeType:name="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;</pre>
 * <p>This declares the things that the activity can do on a directory of
 * notes.  The type being supported is given with the &lt;type&gt; tag, where
 * <code>vnd.android.cursor.dir/vnd.google.note</code> is a URI from which
 * a Cursor of zero or more items (<code>vnd.android.cursor.dir</code>) can
 * be retrieved which holds our note pad data (<code>vnd.google.note</code>).
 * The activity allows the user to view or edit the directory of data (via
 * the VIEW and EDIT actions), or to pick a particular note and return it
 * to the caller (via the PICK action).  Note also the DEFAULT category
 * supplied here: this is <em>required</em> for the
 * {@link Context#startActivity Context.startActivity} method to resolve your
 * activity when its component name is not explicitly specified.</p>
 * <li><pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_GET_CONTENT android.intent.action.GET_CONTENT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;</pre>
 * <p>This filter describes the ability return to the caller a note selected by
 * the user without needing to know where it came from.  The data type
 * <code>vnd.android.cursor.item/vnd.google.note</code> is a URI from which
 * a Cursor of exactly one (<code>vnd.android.cursor.item</code>) item can
 * be retrieved which contains our note pad data (<code>vnd.google.note</code>).
 * The GET_CONTENT action is similar to the PICK action, where the activity
 * will return to its caller a piece of data selected by the user.  Here,
 * however, the caller specifies the type of data they desire instead of
 * the type of data the user will be picking from.</p>
 * </ol>
 *
 * <p>Given these capabilities, the following intents will resolve to the
 * NotesList activity:</p>
 *
 * <ul>
 *     <li> <p><b>{ action=android.app.action.MAIN }</b> matches all of the
 *         activities that can be used as top-level entry points into an
 *         application.</p>
 *     <li> <p><b>{ action=android.app.action.MAIN,
 *         category=android.app.category.LAUNCHER }</b> is the actual intent
 *         used by the Launcher to populate its top-level list.</p>
 *     <li> <p><b>{ action=android.intent.action.VIEW
 *          data=content://com.google.provider.NotePad/notes }</b>
 *         displays a list of all the notes under
 *         "content://com.google.provider.NotePad/notes", which
 *         the user can browse through and see the details on.</p>
 *     <li> <p><b>{ action=android.app.action.PICK
 *          data=content://com.google.provider.NotePad/notes }</b>
 *         provides a list of the notes under
 *         "content://com.google.provider.NotePad/notes", from which
 *         the user can pick a note whose data URL is returned back to the caller.</p>
 *     <li> <p><b>{ action=android.app.action.GET_CONTENT
 *          type=vnd.android.cursor.item/vnd.google.note }</b>
 *         is similar to the pick action, but allows the caller to specify the
 *         kind of data they want back so that the system can find the appropriate
 *         activity to pick something of that data type.</p>
 * </ul>
 *
 * <p>The second activity,
 * <code>com.android.notepad.NoteEditor</code>, shows the user a single
 * note entry and allows them to edit it.  It can do two things as described
 * by its two intent templates:
 * <ol>
 * <li><pre>
 * &lt;intent-filter android:label="@string/resolve_edit"&gt;
 *     &lt;action android:name="{@link #ACTION_VIEW android.intent.action.VIEW}" /&gt;
 *     &lt;action android:name="{@link #ACTION_EDIT android.intent.action.EDIT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;</pre>
 * <p>The first, primary, purpose of this activity is to let the user interact
 * with a single note, as decribed by the MIME type
 * <code>vnd.android.cursor.item/vnd.google.note</code>.  The activity can
 * either VIEW a note or allow the user to EDIT it.  Again we support the
 * DEFAULT category to allow the activity to be launched without explicitly
 * specifying its component.</p>
 * <li><pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_INSERT android.intent.action.INSERT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;</pre>
 * <p>The secondary use of this activity is to insert a new note entry into
 * an existing directory of notes.  This is used when the user creates a new
 * note: the INSERT action is executed on the directory of notes, causing
 * this activity to run and have the user create the new note data which
 * it then adds to the content provider.</p>
 * </ol>
 *
 * <p>Given these capabilities, the following intents will resolve to the
 * NoteEditor activity:</p>
 *
 * <ul>
 *     <li> <p><b>{ action=android.intent.action.VIEW
 *          data=content://com.google.provider.NotePad/notes/<var>{ID}</var> }</b>
 *         shows the user the content of note <var>{ID}</var>.</p>
 *     <li> <p><b>{ action=android.app.action.EDIT
 *          data=content://com.google.provider.NotePad/notes/<var>{ID}</var> }</b>
 *         allows the user to edit the content of note <var>{ID}</var>.</p>
 *     <li> <p><b>{ action=android.app.action.INSERT
 *          data=content://com.google.provider.NotePad/notes }</b>
 *         creates a new, empty note in the notes list at
 *         "content://com.google.provider.NotePad/notes"
 *         and allows the user to edit it.  If they keep their changes, the URI
 *         of the newly created note is returned to the caller.</p>
 * </ul>
 *
 * <p>The last activity,
 * <code>com.android.notepad.TitleEditor</code>, allows the user to
 * edit the title of a note.  This could be implemented as a class that the
 * application directly invokes (by explicitly setting its component in
 * the Intent), but here we show a way you can publish alternative
 * operations on existing data:</p>
 *
 * <pre>
 * &lt;intent-filter android:label="@string/resolve_title"&gt;
 *     &lt;action android:name="<i>com.android.notepad.action.EDIT_TITLE</i>" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_ALTERNATIVE android.intent.category.ALTERNATIVE}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_SELECTED_ALTERNATIVE android.intent.category.SELECTED_ALTERNATIVE}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;</pre>
 *
 * <p>In the single intent template here, we
 * have created our own private action called
 * <code>com.android.notepad.action.EDIT_TITLE</code> which means to
 * edit the title of a note.  It must be invoked on a specific note
 * (data type <code>vnd.android.cursor.item/vnd.google.note</code>) like the previous
 * view and edit actions, but here displays and edits the title contained
 * in the note data.
 *
 * <p>In addition to supporting the default category as usual, our title editor
 * also supports two other standard categories: ALTERNATIVE and
 * SELECTED_ALTERNATIVE.  Implementing
 * these categories allows others to find the special action it provides
 * without directly knowing about it, through the
 * {@link android.content.pm.PackageManager#queryIntentActivityOptions} method, or
 * more often to build dynamic menu items with
 * {@link android.view.Menu#addIntentOptions}.  Note that in the intent
 * template here was also supply an explicit name for the template
 * (via <code>android:label="@string/resolve_title"</code>) to better control
 * what the user sees when presented with this activity as an alternative
 * action to the data they are viewing.
 *
 * <p>Given these capabilities, the following intent will resolve to the
 * TitleEditor activity:</p>
 *
 * <ul>
 *     <li> <p><b>{ action=com.android.notepad.action.EDIT_TITLE
 *          data=content://com.google.provider.NotePad/notes/<var>{ID}</var> }</b>
 *         displays and allows the user to edit the title associated
 *         with note <var>{ID}</var>.</p>
 * </ul>
 *
 * <h3>Standard Activity Actions</h3>
 *
 * <p>These are the current standard actions that Intent defines for launching
 * activities (usually through {@link Context#startActivity}.  The most
 * important, and by far most frequently used, are {@link #ACTION_MAIN} and
 * {@link #ACTION_EDIT}.
 *
 * <ul>
 *     <li> {@link #ACTION_MAIN}
 *     <li> {@link #ACTION_VIEW}
 *     <li> {@link #ACTION_ATTACH_DATA}
 *     <li> {@link #ACTION_EDIT}
 *     <li> {@link #ACTION_PICK}
 *     <li> {@link #ACTION_CHOOSER}
 *     <li> {@link #ACTION_GET_CONTENT}
 *     <li> {@link #ACTION_DIAL}
 *     <li> {@link #ACTION_CALL}
 *     <li> {@link #ACTION_SEND}
 *     <li> {@link #ACTION_SENDTO}
 *     <li> {@link #ACTION_ANSWER}
 *     <li> {@link #ACTION_INSERT}
 *     <li> {@link #ACTION_DELETE}
 *     <li> {@link #ACTION_RUN}
 *     <li> {@link #ACTION_SYNC}
 *     <li> {@link #ACTION_PICK_ACTIVITY}
 *     <li> {@link #ACTION_SEARCH}
 *     <li> {@link #ACTION_WEB_SEARCH}
 *     <li> {@link #ACTION_FACTORY_TEST}
 * </ul>
 *
 * <h3>Standard Broadcast Actions</h3>
 *
 * <p>These are the current standard actions that Intent defines for receiving
 * broadcasts (usually through {@link Context#registerReceiver} or a
 * &lt;receiver&gt; tag in a manifest).
 *
 * <ul>
 *     <li> {@link #ACTION_TIME_TICK}
 *     <li> {@link #ACTION_TIME_CHANGED}
 *     <li> {@link #ACTION_TIMEZONE_CHANGED}
 *     <li> {@link #ACTION_BOOT_COMPLETED}
 *     <li> {@link #ACTION_PACKAGE_ADDED}
 *     <li> {@link #ACTION_PACKAGE_CHANGED}
 *     <li> {@link #ACTION_PACKAGE_REMOVED}
 *     <li> {@link #ACTION_PACKAGE_RESTARTED}
 *     <li> {@link #ACTION_PACKAGE_DATA_CLEARED}
 *     <li> {@link #ACTION_UID_REMOVED}
 *     <li> {@link #ACTION_BATTERY_CHANGED}
 *     <li> {@link #ACTION_POWER_CONNECTED}
 *     <li> {@link #ACTION_POWER_DISCONNECTED}
 *     <li> {@link #ACTION_SHUTDOWN}
 * </ul>
 *
 * <h3>Standard Categories</h3>
 *
 * <p>These are the current standard categories that can be used to further
 * clarify an Intent via {@link #addCategory}.
 *
 * <ul>
 *     <li> {@link #CATEGORY_DEFAULT}
 *     <li> {@link #CATEGORY_BROWSABLE}
 *     <li> {@link #CATEGORY_TAB}
 *     <li> {@link #CATEGORY_ALTERNATIVE}
 *     <li> {@link #CATEGORY_SELECTED_ALTERNATIVE}
 *     <li> {@link #CATEGORY_LAUNCHER}
 *     <li> {@link #CATEGORY_INFO}
 *     <li> {@link #CATEGORY_HOME}
 *     <li> {@link #CATEGORY_PREFERENCE}
 *     <li> {@link #CATEGORY_TEST}
 *     <li> {@link #CATEGORY_CAR_DOCK}
 *     <li> {@link #CATEGORY_DESK_DOCK}
 *     <li> {@link #CATEGORY_LE_DESK_DOCK}
 *     <li> {@link #CATEGORY_HE_DESK_DOCK}
 *     <li> {@link #CATEGORY_CAR_MODE}
 *     <li> {@link #CATEGORY_APP_MARKET}
 * </ul>
 *
 * <h3>Standard Extra Data</h3>
 *
 * <p>These are the current standard fields that can be used as extra data via
 * {@link #putExtra}.
 *
 * <ul>
 *     <li> {@link #EXTRA_ALARM_COUNT}
 *     <li> {@link #EXTRA_BCC}
 *     <li> {@link #EXTRA_CC}
 *     <li> {@link #EXTRA_CHANGED_COMPONENT_NAME}
 *     <li> {@link #EXTRA_DATA_REMOVED}
 *     <li> {@link #EXTRA_DOCK_STATE}
 *     <li> {@link #EXTRA_DOCK_STATE_HE_DESK}
 *     <li> {@link #EXTRA_DOCK_STATE_LE_DESK}
 *     <li> {@link #EXTRA_DOCK_STATE_CAR}
 *     <li> {@link #EXTRA_DOCK_STATE_DESK}
 *     <li> {@link #EXTRA_DOCK_STATE_UNDOCKED}
 *     <li> {@link #EXTRA_DONT_KILL_APP}
 *     <li> {@link #EXTRA_EMAIL}
 *     <li> {@link #EXTRA_INITIAL_INTENTS}
 *     <li> {@link #EXTRA_INTENT}
 *     <li> {@link #EXTRA_KEY_EVENT}
 *     <li> {@link #EXTRA_ORIGINATING_URI}
 *     <li> {@link #EXTRA_PHONE_NUMBER}
 *     <li> {@link #EXTRA_REFERRER}
 *     <li> {@link #EXTRA_REMOTE_INTENT_TOKEN}
 *     <li> {@link #EXTRA_REPLACING}
 *     <li> {@link #EXTRA_SHORTCUT_ICON}
 *     <li> {@link #EXTRA_SHORTCUT_ICON_RESOURCE}
 *     <li> {@link #EXTRA_SHORTCUT_INTENT}
 *     <li> {@link #EXTRA_STREAM}
 *     <li> {@link #EXTRA_SHORTCUT_NAME}
 *     <li> {@link #EXTRA_SUBJECT}
 *     <li> {@link #EXTRA_TEMPLATE}
 *     <li> {@link #EXTRA_TEXT}
 *     <li> {@link #EXTRA_TITLE}
 *     <li> {@link #EXTRA_UID}
 * </ul>
 *
 * <h3>Flags</h3>
 *
 * <p>These are the possible flags that can be used in the Intent via
 * {@link #setFlags} and {@link #addFlags}.  See {@link #setFlags} for a list
 * of all possible flags.
 */
public class Intent implements Parcelable, Cloneable {


    // ---------------------------------------------------------------------

    private String mAction;
    private Bundle mExtras;
    

    // ---------------------------------------------------------------------

    /**
     * Create an empty intent.
     */
    public Intent() {
    }

    /**
     * Create an intent with a given action.  All other fields (data, type,
     * class) are null.  Note that the action <em>must</em> be in a
     * namespace because Intents are used globally in the system -- for
     * example the system VIEW action is android.intent.action.VIEW; an
     * application's custom action would be something like
     * com.google.app.myapp.CUSTOM_ACTION.
     *
     * @param action The Intent action, such as ACTION_VIEW.
     */
    public Intent(String action) {
        setAction(action);
    }

    /**
     * Retrieve the general action to be performed, such as
     * {@link #ACTION_VIEW}.  The action describes the general way the rest of
     * the information in the intent should be interpreted -- most importantly,
     * what to do with the data returned by {@link #getData}.
     *
     * @return The action of this intent or null if none is specified.
     *
     * @see #setAction
     */
    public String getAction() {
        return mAction;
    }

    /**
     * Sets the ClassLoader that will be used when unmarshalling
     * any Parcelable values from the extras of this Intent.
     *
     * @param loader a ClassLoader, or null to use the default loader
     * at the time of unmarshalling.
     */
    public void setExtrasClassLoader(ClassLoader loader) {
        if (mExtras != null) {
            mExtras.setClassLoader(loader);
        }
    }

    /**
     * Returns true if an extra value is associated with the given name.
     * @param name the extra's name
     * @return true if the given extra is present.
     */
    public boolean hasExtra(String name) {
        return mExtras != null && mExtras.containsKey(name);
    }

    /**
     * Returns true if the Intent's extras contain a parcelled file descriptor.
     * @return true if the Intent contains a parcelled file descriptor.
     */
    public boolean hasFileDescriptors() {
        return mExtras != null && mExtras.hasFileDescriptors();
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if none was found.
     *
     * @deprecated
     * @hide
     */
    @Deprecated
    public Object getExtra(String name) {
        return getExtra(name, null);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, boolean)
     */
    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getBoolean(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, byte)
     */
    public byte getByteExtra(String name, byte defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getByte(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, short)
     */
    public short getShortExtra(String name, short defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getShort(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, char)
     */
    public char getCharExtra(String name, char defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getChar(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, int)
     */
    public int getIntExtra(String name, int defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getInt(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, long)
     */
    public long getLongExtra(String name, long defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getLong(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra(),
     * or the default value if no such item is present
     *
     * @see #putExtra(String, float)
     */
    public float getFloatExtra(String name, float defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getFloat(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired
     * type is stored with the given name.
     *
     * @return the value of an item that previously added with putExtra()
     * or the default value if none was found.
     *
     * @see #putExtra(String, double)
     */
    public double getDoubleExtra(String name, double defaultValue) {
        return mExtras == null ? defaultValue :
            mExtras.getDouble(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no String value was found.
     *
     * @see #putExtra(String, String)
     */
    public String getStringExtra(String name) {
        return mExtras == null ? null : mExtras.getString(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no CharSequence value was found.
     *
     * @see #putExtra(String, CharSequence)
     */
    public CharSequence getCharSequenceExtra(String name) {
        return mExtras == null ? null : mExtras.getCharSequence(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no Parcelable value was found.
     *
     * @see #putExtra(String, Parcelable)
     */
    public <T extends Parcelable> T getParcelableExtra(String name) {
        return mExtras == null ? null : mExtras.<T>getParcelable(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no Parcelable[] value was found.
     *
     * @see #putExtra(String, Parcelable[])
     */
    public Parcelable[] getParcelableArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getParcelableArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no ArrayList<Parcelable> value was found.
     *
     * @see #putParcelableArrayListExtra(String, ArrayList)
     */
    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name) {
        return mExtras == null ? null : mExtras.<T>getParcelableArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no Serializable value was found.
     *
     * @see #putExtra(String, Serializable)
     */
    public Serializable getSerializableExtra(String name) {
        return mExtras == null ? null : mExtras.getSerializable(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no ArrayList<Integer> value was found.
     *
     * @see #putIntegerArrayListExtra(String, ArrayList)
     */
    public ArrayList<Integer> getIntegerArrayListExtra(String name) {
        return mExtras == null ? null : mExtras.getIntegerArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no ArrayList<String> value was found.
     *
     * @see #putStringArrayListExtra(String, ArrayList)
     */
    public ArrayList<String> getStringArrayListExtra(String name) {
        return mExtras == null ? null : mExtras.getStringArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no ArrayList<CharSequence> value was found.
     *
     * @see #putCharSequenceArrayListExtra(String, ArrayList)
     */
    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
        return mExtras == null ? null : mExtras.getCharSequenceArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no boolean array value was found.
     *
     * @see #putExtra(String, boolean[])
     */
    public boolean[] getBooleanArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getBooleanArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no byte array value was found.
     *
     * @see #putExtra(String, byte[])
     */
    public byte[] getByteArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getByteArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no short array value was found.
     *
     * @see #putExtra(String, short[])
     */
    public short[] getShortArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getShortArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no char array value was found.
     *
     * @see #putExtra(String, char[])
     */
    public char[] getCharArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getCharArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no int array value was found.
     *
     * @see #putExtra(String, int[])
     */
    public int[] getIntArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getIntArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no long array value was found.
     *
     * @see #putExtra(String, long[])
     */
    public long[] getLongArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getLongArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no float array value was found.
     *
     * @see #putExtra(String, float[])
     */
    public float[] getFloatArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getFloatArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no double array value was found.
     *
     * @see #putExtra(String, double[])
     */
    public double[] getDoubleArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getDoubleArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no String array value was found.
     *
     * @see #putExtra(String, String[])
     */
    public String[] getStringArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getStringArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no CharSequence array value was found.
     *
     * @see #putExtra(String, CharSequence[])
     */
    public CharSequence[] getCharSequenceArrayExtra(String name) {
        return mExtras == null ? null : mExtras.getCharSequenceArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item that previously added with putExtra()
     * or null if no Bundle value was found.
     *
     * @see #putExtra(String, Bundle)
     */
    public Bundle getBundleExtra(String name) {
        return mExtras == null ? null : mExtras.getBundle(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @param defaultValue The default value to return in case no item is
     * associated with the key 'name'
     *
     * @return the value of an item that previously added with putExtra()
     * or defaultValue if none was found.
     *
     * @see #putExtra
     *
     * @deprecated
     * @hide
     */
    @Deprecated
    public Object getExtra(String name, Object defaultValue) {
        Object result = defaultValue;
        if (mExtras != null) {
            Object result2 = mExtras.get(name);
            if (result2 != null) {
                result = result2;
            }
        }

        return result;
    }

    /**
     * Retrieves a map of extended data from the intent.
     *
     * @return the map of all extras previously added with putExtra(),
     * or null if none have been added.
     */
    public Bundle getExtras() {
        return (mExtras != null)
                ? new Bundle(mExtras)
                : null;
    }

    /**
     * Set the general action to be performed.
     *
     * @param action An action name, such as ACTION_VIEW.  Application-specific
     *               actions should be prefixed with the vendor's package name.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #getAction
     */
    public Intent setAction(String action) {
        mAction = action != null ? action.intern() : null;
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The boolean data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanExtra(String, boolean)
     */
    public Intent putExtra(String name, boolean value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBoolean(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The byte data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteExtra(String, byte)
     */
    public Intent putExtra(String name, byte value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putByte(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The char data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharExtra(String, char)
     */
    public Intent putExtra(String name, char value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putChar(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The short data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortExtra(String, short)
     */
    public Intent putExtra(String name, short value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putShort(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The integer data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntExtra(String, int)
     */
    public Intent putExtra(String name, int value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putInt(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The long data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongExtra(String, long)
     */
    public Intent putExtra(String name, long value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putLong(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The float data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatExtra(String, float)
     */
    public Intent putExtra(String name, float value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putFloat(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The double data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleExtra(String, double)
     */
    public Intent putExtra(String name, double value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putDouble(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The String data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringExtra(String)
     */
    public Intent putExtra(String name, String value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putString(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The CharSequence data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceExtra(String)
     */
    public Intent putExtra(String name, CharSequence value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharSequence(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The Parcelable data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableExtra(String)
     */
    public Intent putExtra(String name, Parcelable value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putParcelable(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The Parcelable[] data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayExtra(String)
     */
    public Intent putExtra(String name, Parcelable[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putParcelableArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<Parcelable> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayListExtra(String)
     */
    public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putParcelableArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<Integer> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntegerArrayListExtra(String)
     */
    public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putIntegerArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<String> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayListExtra(String)
     */
    public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putStringArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The ArrayList<CharSequence> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayListExtra(String)
     */
    public Intent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharSequenceArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The Serializable data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getSerializableExtra(String)
     */
    public Intent putExtra(String name, Serializable value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putSerializable(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The boolean array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanArrayExtra(String)
     */
    public Intent putExtra(String name, boolean[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBooleanArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The byte array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteArrayExtra(String)
     */
    public Intent putExtra(String name, byte[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putByteArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The short array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortArrayExtra(String)
     */
    public Intent putExtra(String name, short[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putShortArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The char array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharArrayExtra(String)
     */
    public Intent putExtra(String name, char[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The int array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntArrayExtra(String)
     */
    public Intent putExtra(String name, int[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putIntArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The byte array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongArrayExtra(String)
     */
    public Intent putExtra(String name, long[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putLongArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The float array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatArrayExtra(String)
     */
    public Intent putExtra(String name, float[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putFloatArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The double array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleArrayExtra(String)
     */
    public Intent putExtra(String name, double[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putDoubleArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The String array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayExtra(String)
     */
    public Intent putExtra(String name, String[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putStringArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The CharSequence array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayExtra(String)
     */
    public Intent putExtra(String name, CharSequence[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putCharSequenceArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name The name of the extra data, with package prefix.
     * @param value The Bundle data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBundleExtra(String)
     */
    public Intent putExtra(String name, Bundle value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBundle(name, value);
        return this;
    }

    /**
     * Copy all extras in 'src' in to this intent.
     *
     * @param src Contains the extras to copy.
     *
     * @see #putExtra
     */
    public Intent putExtras(Intent src) {
        if (src.mExtras != null) {
            if (mExtras == null) {
                mExtras = new Bundle(src.mExtras);
            } else {
                mExtras.putAll(src.mExtras);
            }
        }
        return this;
    }

    /**
     * Add a set of extended data to the intent.  The keys must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param extras The Bundle of extras to add to this intent.
     *
     * @see #putExtra
     * @see #removeExtra
     */
    public Intent putExtras(Bundle extras) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putAll(extras);
        return this;
    }

    /**
     * Completely replace the extras in the Intent with the given Bundle of
     * extras.
     *
     * @param extras The new set of extras in the Intent, or null to erase
     * all extras.
     */
    public Intent replaceExtras(Bundle extras) {
        mExtras = extras != null ? new Bundle(extras) : null;
        return this;
    }

    /**
     * Remove extended data from the intent.
     *
     * @see #putExtra
     */
    public void removeExtra(String name) {
        if (mExtras != null) {
            mExtras.remove(name);
            if (mExtras.size() == 0) {
                mExtras = null;
            }
        }
    }

    /**
     * Use with {@link #fillIn} to allow the current action value to be
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_ACTION = 1<<0;

    /**
     * Use with {@link #fillIn} to allow the current data or type value
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_DATA = 1<<1;

    /**
     * Use with {@link #fillIn} to allow the current categories to be
     * overwritten, even if they are already set.
     */
    public static final int FILL_IN_CATEGORIES = 1<<2;

    /**
     * Use with {@link #fillIn} to allow the current component value to be
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_COMPONENT = 1<<3;

    /**
     * Use with {@link #fillIn} to allow the current package value to be
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_PACKAGE = 1<<4;

    /**
     * Use with {@link #fillIn} to allow the current bounds rectangle to be
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_SOURCE_BOUNDS = 1<<5;

    /**
     * Use with {@link #fillIn} to allow the current selector to be
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_SELECTOR = 1<<6;

    /**
     * Use with {@link #fillIn} to allow the current ClipData to be
     * overwritten, even if it is already set.
     */
    public static final int FILL_IN_CLIP_DATA = 1<<7;

    /**
     * Wrapper class holding an Intent and implementing comparisons on it for
     * the purpose of filtering.  The class implements its
     * {@link #equals equals()} and {@link #hashCode hashCode()} methods as
     * simple calls to {@link Intent#filterEquals(Intent)}  filterEquals()} and
     * {@link android.content.Intent#filterHashCode()}  filterHashCode()}
     * on the wrapped Intent.
     */
    public static final class FilterComparison {
        private final Intent mIntent;
        private final int mHashCode;

        public FilterComparison(Intent intent) {
            mIntent = intent;
            mHashCode = intent.filterHashCode();
        }

        /**
         * Return the Intent that this FilterComparison represents.
         * @return Returns the Intent held by the FilterComparison.  Do
         * not modify!
         */
        public Intent getIntent() {
            return mIntent;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FilterComparison) {
                Intent other = ((FilterComparison)obj).mIntent;
                return mIntent.filterEquals(other);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return mHashCode;
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(128);

        b.append("Letter { ");
        toShortString(b, true, true, true, false);
        b.append(" }");

        return b.toString();
    }

    /** @hide */
    public String toInsecureString() {
        StringBuilder b = new StringBuilder(128);

        b.append("Letter { ");
        toShortString(b, false, true, true, false);
        b.append(" }");

        return b.toString();
    }

    /** @hide */
    public String toInsecureStringWithClip() {
        StringBuilder b = new StringBuilder(128);

        b.append("Intent { ");
        toShortString(b, false, true, true, true);
        b.append(" }");

        return b.toString();
    }

    /** @hide */
    public String toShortString(boolean secure, boolean comp, boolean extras, boolean clip) {
        StringBuilder b = new StringBuilder(128);
        toShortString(b, secure, comp, extras, clip);
        return b.toString();
    }

    /** @hide */
    public void toShortString(StringBuilder b, boolean secure, boolean comp, boolean extras,
            boolean clip) {
        boolean first = true;
        if (mAction != null) {
            b.append("act=").append(mAction);
            first = false;
        }
        if (extras && mExtras != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("(has extras)");
        }
        b.append("}");
    }

    public int describeContents() {
        return (mExtras != null) ? mExtras.describeContents() : 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mAction);
        out.writeBundle(mExtras);
    }

    public static final Parcelable.Creator<Intent> CREATOR
            = new Parcelable.Creator<Intent>() {
        public Intent createFromParcel(Parcel in) {
            return new Intent(in);
        }
        public Intent[] newArray(int size) {
            return new Intent[size];
        }
    };

    /** @hide */
    protected Intent(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        setAction(in.readString());
        mExtras = in.readBundle();
    }
}
