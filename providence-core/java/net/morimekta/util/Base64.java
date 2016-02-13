/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.morimekta.util;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Base64 encoding for providence utils. Will always encode with URL_SAFE encoding, and will accept both URL_SAFE and atandard
 */
public class Base64 {
    /* ********  P R I V A T E   F I E L D S  ******** */

    /** The equals sign (=) as a byte. */
    protected final static byte EQUALS_SIGN = (byte) '=';

    protected final static byte WHITE_SPACE_ENC = -5; // Indicates white space in encoding
    private final static byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in encoding

    /* ********  U R L   S A F E   B A S E 6 4   A L P H A B E T  ******** */

    /**
     * Used in the URL- and Filename-safe dialect described in Section 4 of RFC3548:
     * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
     * Notice that the last two bytes become "hyphen" and "underscore" instead of "plus" and "slash."
     */
    private final static byte[] _URL_SAFE_ALPHABET = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H',
            (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P',
            (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',
            (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v',
            (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'
    };

    /**
     * Used in decoding URL- and Filename-safe dialects of Base64.
     */
    private final static byte[] _URL_SAFE_DECODABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9,                  // Decimal  0 -  8
            -5, -5,                                              // Whitespace: Tab and Linefeed
            -9, -9,                                              // Decimal 11 - 12
            -5,                                                  // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,  // Decimal 14 - 26
            -9, -9, -9, -9, -9,                                  // Decimal 27 - 31
            -5,                                                  // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,              // Decimal 33 - 42
            62,                                                  // Plus sign at decimal 43
            -9,                                                  // Decimal 44
            62,                                                  // Minus sign at decimal 45
            -9,                                                  // Decimal 46
            63,                                                  // Slash at decimal 47
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61,              // Numbers zero through nine
            -9, -9, -9,                                          // Decimal 58 - 60
            -1,                                                  // Equals sign at decimal 61
            -9, -9, -9,                                          // Decimal 62 - 64
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,        // Letters 'A' through 'N'
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,      // Letters 'O' through 'Z'
            -9, -9, -9, -9,                                      // Decimal 91 - 94
            63,                                                  // Underscore at decimal 95
            -9,                                                  // Decimal 96
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,  // Letters 'a' through 'm'
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,  // Letters 'n' through 'z'
            -9, -9, -9, -9, -9,                                  // Decimal 123 - 127
    };

    /* ********  D E T E R M I N E   W H I C H   A L H A B E T  ******** */

    /** Defeats instantiation. */
    private Base64() {
    }

    /* ********  E N C O D I N G   M E T H O D S  ******** */

    /**
     * <p>Encodes up to three bytes of the array <var>source</var>
     * and writes the resulting four Base64 bytes to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 3 for
     * the <var>source</var> array or <var>destOffset</var> + 4 for
     * the <var>destination</var> array.
     * The actual number of significant bytes in your array is
     * given by <var>numSigBytes</var>.</p>
     * <p>This is the lowest level of the encoding methods with
     * all possible parameters.</p>
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the <var>destination</var> array
     * @since 1.3
     */
    protected static int encode3to4(byte[] source,
                                    int srcOffset,
                                    int numSigBytes,
                                    byte[] destination,
                                    int destOffset) {
        //           1         2         3
        // 01234567890123456789012345678901 Bit position
        // --------000000001111111122222222 Array position from threeBytes
        // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
        //          >>18  >>12  >> 6  >> 0  Right shift necessary
        //                0x3f  0x3f  0x3f  Additional AND

        // Create buffer with zero-padding if there are only one or two
        // significant bytes passed in the array.
        // We have to shift left 24 in order to flush out the 1's that appear
        // when Java treats a value as negative that is cast from a byte to an int.
        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) |
                     (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) |
                     (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

        switch (numSigBytes) {
            case 3:
                destination[destOffset] = _URL_SAFE_ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = _URL_SAFE_ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = _URL_SAFE_ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = _URL_SAFE_ALPHABET[(inBuff) & 0x3f];
                return 4;

            case 2:
                destination[destOffset] = _URL_SAFE_ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = _URL_SAFE_ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = _URL_SAFE_ALPHABET[(inBuff >>> 6) & 0x3f];
                return 3;

            case 1:
                destination[destOffset] = _URL_SAFE_ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = _URL_SAFE_ALPHABET[(inBuff >>> 12) & 0x3f];
                return 2;

            default:
                return 0;
        }   // end switch
    }   // end encode3to4

    /**
     * Encodes a byte array into Base64 notation.
     *
     * @param source The data to convert
     * @return The Base64-encoded data as a String
     * @throws NullPointerException if source array is null
     * @since 2.0
     */
    public static String encodeToString(byte[] source) {
        return encodeToString(source, 0, source.length);
    }

    public static String encodeToString(byte[] source, int off, int len) {
        byte[] encoded = encode(source, off, len);
        return new String(encoded, UTF_8);
    }

    public static byte[] encode(final byte[] source, final int off, final int len) {
        if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
        }   // end if: null

        if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        }   // end if: off < 0

        if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
        }   // end if: len < 0

        if (off + len > source.length) {
            throw new IllegalArgumentException(String.format(
                    "Cannot have offset of %d and length of %d with array of length %d",
                    off,
                    len,
                    source.length));
        }   // end if: off < 0

        if (len == 0) {
            return new byte[0];
        }

        //int    len43   = len * 4 / 3;
        //byte[] outBuff = new byte[   ( len43 )                      // Main 4:3
        //                           + ( (len % 3) > 0 ? 4 : 0 )      // Account for padding
        //                           + (breakLines ? ( len43 / MAX_LINE_LENGTH ) : 0) ]; // New lines
        // Try to determine more precisely how big the array needs to be.
        // If we get it right, we don't have to do an array copy, and
        // we save a bunch of memory.
        int bufLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0); // Bytes needed for actual encoding
        byte[] dest = new byte[bufLen];

        int srcPos = 0;
        int destPos = 0;
        final int len2 = len - 2;
        for (; srcPos < len2; srcPos += 3) {
            destPos += encode3to4(source, srcPos + off, 3, dest, destPos);
        }   // end for: each piece of array

        if (srcPos < len) {
            destPos += encode3to4(source, srcPos + off, len - srcPos, dest, destPos);
        }   // end if: some padding needed

        // Only resize array if we didn't guess it right.
        if (destPos < dest.length) {
            // If breaking lines and the last byte falls right at
            // the line length (76 bytes per line), there will be
            // one extra byte, and the array will need to be resized.
            // Not too bad of an estimate on array size, I'd say.
            byte[] finalOut = new byte[destPos];
            System.arraycopy(dest, 0, finalOut, 0, destPos);
            return finalOut;
        } else {
            return dest;
        }

    }   // end encode

    public static byte[] encode(byte[] source) {
        return encode(source, 0, source.length);
    }

    /* ********  D E C O D I N G   M E T H O D S  ******** */

    /**
     * Decodes four bytes from array <var>source</var>
     * and writes the resulting bytes (up to three of them)
     * to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 4 for
     * the <var>source</var> array or <var>destOffset</var> + 3 for
     * the <var>destination</var> array.
     * This method returns the actual number of bytes that
     * were converted from the Base64 encoding.
     * <p>This is the lowest level of the decoding methods with
     * all possible parameters.</p>
     *
     *
     * @param src the array to convert
     * @param srcOffset the index where conversion begins
     * @param dest the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the number of decoded bytes converted
     * @throws NullPointerException if source or destination arrays are null
     * @throws IllegalArgumentException if srcOffset or destOffset are invalid
     *         or there is not enough room in the array.
     * @since 1.3
     */
    protected static int decode4to3(byte[] src,
                                    int srcOffset,
                                    int srcLen,
                                    byte[] dest,
                                    int destOffset) {
        if (srcLen < 2) {
            throw new IllegalArgumentException(String.format(
                    "Source array with length %d cannot have offset of %d.",
                    src.length,
                    srcOffset));
        }
        if (srcOffset < 0 || srcOffset + srcLen > src.length) {
            throw new IllegalArgumentException(String.format(
                    "Source array with length %d cannot have offset of %d and still process four bytes.",
                    src.length,
                    srcOffset));
        }   // end if
        if (destOffset < 0 || destOffset + (srcLen - 1) > dest.length) {
            throw new IllegalArgumentException(String.format(
                    "Destination array with length %d cannot have offset of %d and still store three bytes.",
                    dest.length,
                    destOffset));
        }   // end if

        // Example: Dk or Dk==
        if (srcLen == 2 || (src[srcOffset + 2] == EQUALS_SIGN && src[srcOffset + 3] == EQUALS_SIGN)) {
            int outBuff =
                    (validate(src[srcOffset])     << 18) |
                    (validate(src[srcOffset + 1]) << 12);

            dest[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        }

        // Example: DkL or DkL=
        else if (srcLen == 3 || src[srcOffset + 3] == EQUALS_SIGN) {
            int outBuff =
                    (validate(src[srcOffset])     << 18) |
                    (validate(src[srcOffset + 1]) << 12) |
                    (validate(src[srcOffset + 2]) << 6);

            dest[destOffset] = (byte) (outBuff >>> 16);
            dest[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        }

        // Example: DkLE
        else {
            int outBuff =
                    (validate(src[srcOffset])     << 18) |
                    (validate(src[srcOffset + 1]) << 12) |
                    (validate(src[srcOffset + 2]) << 6) |
                    (validate(src[srcOffset + 3]));

            dest[destOffset] = (byte) (outBuff >> 16);
            dest[destOffset + 1] = (byte) (outBuff >> 8);
            dest[destOffset + 2] = (byte) (outBuff);

            return 3;
        }
    }

    private static int validate(byte from) {
        byte b = _URL_SAFE_DECODABET[from & 0x7F];
        if (b < 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid%s url safe base64 character \\u%04d",
                    (b & 0xff)));
        }
        return b;
    }

    /**
     * Decode the Base64-encoded data in input and return the data in a new
     * byte array.
     *
     * The padding '=' characters at the end are considered optional, but if
     * any are present, there must be the correct number of them.
     *
     * @param source The data to decode
     * @return decoded data
     */
    public static byte[] decode(byte[] source) throws IOException {
        return decode(source, 0, source.length);
    }

    /**
     * Decode the Base64-encoded data in input and return the data in a new
     * byte array.
     *
     * The padding '=' characters at the end are considered optional, but if
     * any are present, there must be the correct number of them.
     *
     * @param source The data to decode
     * @param offset The offset within the input array at which to start
     * @param len    The number of byte sof input to decode.
     * @return decoded data
     */
    public static byte[] decode(byte[] source, int offset, int len) {
        if (source == null) {
            throw new NullPointerException("Cannot decode null source array.");
        }
        if (offset < 0 || offset + len > source.length) {
            throw new IllegalArgumentException(String.format(
                    "Source array with length %d cannot have offset of %d and process %d bytes.",
                    source.length,
                    offset,
                    len));
        }

        if (len == 0) {
            return new byte[0];
        }

        int len34 = len * 3 / 4;          // Estimate on array size
        byte[] outBuff = new byte[len34]; // Upper limit on size of output
        int outBuffPosn = 0;              // Keep track of where we're writing

        byte[] b4 = new byte[4]; // Four byte buffer from source, eliminating white space
        int b4Posn = 0;          // Keep track of four byte input buffer
        int i;                   // Source array counter
        byte sbiDecode;          // Special value from DECODABET

        for (i = offset; i < offset + len; i++) {  // Loop through source
            sbiDecode = _URL_SAFE_DECODABET[source[i] & 0x7F];

            // White space, Equals sign, or legit Base64 character
            // Note the values such as -5 and -9 in the
            // DECODABETs at the top of the file.
            if (sbiDecode >= WHITE_SPACE_ENC) {
                if (sbiDecode >= EQUALS_SIGN_ENC) {
                    b4[b4Posn++] = source[i];
                    if (b4Posn > 3) {
                        outBuffPosn += decode4to3(b4, 0, 4, outBuff, outBuffPosn);
                        b4Posn = 0;
                    }
                }
            } else {
                // There's a bad input character in the Base64 stream.
                throw new IllegalArgumentException(
                        String.format("Bad Base64 input character decimal %d in array position %d",
                                      ((int) source[i]) & 0xFF,
                                      i));
            }
        }

        if (b4Posn > 0) {
            outBuffPosn += decode4to3(b4, 0, b4Posn, outBuff, outBuffPosn);
        }

        if (outBuffPosn < outBuff.length) {
            byte[] out = new byte[outBuffPosn];
            System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
            return out;
        }
        return outBuff;
    }   // end decode

    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
     * @return the decoded data
     * @throws NullPointerException if <tt>s</tt> is null
     */
    public static byte[] decode(String s) {
        if (s == null) {
            throw new NullPointerException("Input string was null.");
        }

        byte[] bytes = s.getBytes(UTF_8);
        return decode(bytes, 0, bytes.length);
    }
}
