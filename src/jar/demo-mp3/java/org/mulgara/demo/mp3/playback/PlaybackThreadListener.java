/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is the Kowari Metadata Store.
 *
 * The Initial Developer of the Original Code is Plugged In Software Pty
 * Ltd (http://www.pisoftware.com, mailto:info@pisoftware.com). Portions
 * created by Plugged In Software Pty Ltd are Copyright (C) 2001,2002
 * Plugged In Software Pty Ltd. All Rights Reserved.
 *
 * Contributor(s): N/A.
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text
 * of the notices in the Source Code files of the Original Code. You
 * should use the text of this Exhibit A rather than the text found in the
 * Original Code Source Code for Your Modifications.]
 *
 */

package org.mulgara.demo.mp3.playback;

// Java 2 standard packages

// JLayer
import javazoom.jl.player.advanced.*;
import org.jrdf.graph.*;

// Local packages

/**
 * Used to notify an PlaybackThread caller of events.
 *
 * @created 2004-12-10
 *
 * @author <a href="mailto:robert.turner@tucanatech.com">Robert Turner</a>
 *
 * @version $Revision$
 *
 * @modified $Date: 2005/01/05 04:58:06 $
 *
 * @maintenanceAuthor $Author: newmana $
 *
 * @company <A href="mailto:info@PIsoftware.com">Plugged In Software</A>
 *
 * @copyright &copy;2001 <a href="http://www.pisoftware.com/">Plugged In
 *   Software Pty Ltd</a>
 *
 * @licence <a href="{@docRoot}/../../LICENCE">Mozilla Public License v1.1</a>
 */
public abstract class PlaybackThreadListener extends PlaybackListener {

  /**
   * Indicates an exception occurred during playback.
   * @param t Throwable
   */
  public abstract void exceptionOccurred(Throwable t);

  /**
   * Indicates that the resource has began playing.
   * @param resource URIReference
   */
  public abstract void playbackStarted(URIReference resource);

  /**
   * Indicates that the thread has finished.
   */
  public abstract void playbackComplete();

  /**
   * Indicates that playback has been paused
   */
  public abstract void playbackPaused();

  /**
   * Indicates that playback has resumed
   */
  public abstract void playbackResumed();
}
