/* Decode.java - 15 janv. 2016  -  UTF-8 - 
 * --------------------------------- DISCLAMER ---------------------------------
 * Copyright (c) 2015, Bastien Enjalbert All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * The views and conclusions contained in the software and documentation are 
 * those of the authors and should not be interpreted as representing official 
 * policies, either expressed or implied, of the FreeBSD Project.
 * @author Bastien Enjalbert
 */
package gsm.topguw.tools;

import gsm.topguw.channels.*;
import gsm.topguw.conf.RtlsdrConf;
import gsm.topguw.err.ChannelError;
import gsm.topguw.generality.Cell;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Decode cfile/bursts file with gr-gsm
 *
 * @author bastien.enjalbert
 */
public class Decode {

    /**
     * List of available channels where we can decode frames
     */
    public static HashMap<String, Channels> availableChan = new HashMap<>();

    /**
     * Register channel type
     *
     * @param T the name of the channel
     * @param C the channel instance
     */
    public static void registerChannel(String T, Channels C) {
        availableChan.put(T, C);
    }

    /**
     * Get a channel to work with
     *
     * @param chanType the channel type to decode
     * @param timeslot the timeslot
     * @param subslot the subslot
     * @param cfile the linked cfile to the channel
     * @return the channel frames and information
     * @throws ChannelError if the argument channel type isn't
     * available/supported
     */
    public static Channels getChannel(String chanType, int timeslot, int subslot, File cfile)
            throws ChannelError {
       
        registerChannel("combined", new Combined());
        registerChannel("noncombined", new NonCombined());
        registerChannel("standalonecontrol", new StandaloneControl());
        registerChannel("traffic", new Traffic());
        
        if (!availableChan.containsKey(chanType)) {
            throw new ChannelError("Channel type isn't supported");
        }
        
        return availableChan.get(chanType).decode(timeslot, subslot, cfile);
    }

    /**
     * Get channel's frame
     *
     * @param channel the channel
     * @param currentCell the current cell  
     * @param rtlconf the current rtl sdr configuration (same as capture file)
     * @param key the key and his version (A1/2/3)
     */
    public static void getChannelFrame(Channels channel, Cell currentCell, 
                                            RtlsdrConf rtlconf, String[] key) {
        try {
            channel.start(currentCell, rtlconf, key);
        } catch(IOException e) {
            System.err.println("An error occur when trying to extract frammes: \n" + e.getMessage());
        }
    }
}