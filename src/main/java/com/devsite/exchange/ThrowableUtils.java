package com.devsite.exchange;


/**
 * Created by wahmed on 6/30/2015.
 */
public class ThrowableUtils
{
    public static boolean isCause(Class<? extends Throwable> expected, Throwable givenException)
    {
        return expected.isInstance(givenException) ||
                (
                    givenException != null &&
                            isCause(expected, givenException.getCause())
                );
    }

    public static boolean doesExceptionMessageContains(String expectedMessage, Throwable givenException)
    {
        if(givenException == null)
            return false;

        String currentMessage = givenException.getMessage() == null ? "" : givenException.getMessage();
        currentMessage = currentMessage.toLowerCase();
        expectedMessage = expectedMessage.toLowerCase();

        return currentMessage.contains(expectedMessage) ||
                (
                        givenException != null &&
                                doesExceptionMessageContains(expectedMessage, givenException.getCause())
                );
    }

    public static boolean isJavaNetworkException( Throwable exc)
    {
        // java.net.SocketTimeoutException extends java.io.InterruptedIOException
        // java.net.SocketException
        //          BindException
        //          ConnectException
        //          NoRouteToHostException
        //          PortUnreachableException
        // java.nio.channels.ClosedChannelException
        //          AsynchronousCloseException
        // java.net.ProtocolException
        // java.net.UnknownHostException
        // java.net.UnknownServiceException

        // also javax.net.ssl.SSLHandshakeException
        // also javax.net.ssl.SSLException
        return isCause(java.net.SocketTimeoutException.class, exc) ||
                isCause(java.net.SocketException.class, exc) ||
                isCause(java.nio.channels.ClosedChannelException.class, exc) ||
                isCause(java.net.ProtocolException.class, exc) ||
                isCause(java.net.UnknownHostException.class, exc) ||
                isCause(java.net.UnknownServiceException.class, exc) ||
                isCause(javax.net.ssl.SSLHandshakeException.class, exc) ||
                isCause(javax.net.ssl.SSLException.class, exc)
                ;

    }
}
