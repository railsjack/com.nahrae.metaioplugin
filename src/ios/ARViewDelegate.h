//
//  ARViewDelegate.h
//  MetaioCordovaTest
//
//  Created by Simone Franchina on 14/05/14.
//
//

#import <Foundation/Foundation.h>

@protocol ARViewDelegate
    -(void)closeAR;
    -(void)executeCallbackWithString:(NSString*)string;
    -(void)handleCall:(NSString*)functionName callbackId:(int)callbackId args:(NSArray*)args closeAR:(BOOL)close webView:(UIWebView*)webview;
@end