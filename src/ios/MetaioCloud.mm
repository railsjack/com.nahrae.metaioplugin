//
//  ViewController.h
//  metaio fundamentals
//
//  Created by Simone on 19/11/13.
//  Copyright (c) 2013 Simone. All rights reserved.
//



/********* MetaioCloud.h Cordova Plugin Implementation *******/

#import "MetaioCloud.h"
#import <Cordova/CDV.h>

@implementation MetaioCloud

@synthesize callback,arview,javascriptSelectors;


- (void)metaiocloud:(CDVInvokedUrlCommand*)command
{
   
   
    NSString *channelString = [command.arguments objectAtIndex:0];
    
    NSMutableArray* args = [NSMutableArray arrayWithArray:command.arguments];
    [args removeObjectAtIndex:0];

    [self setJavascriptSelectors:args];
    
    
    int channel = [channelString intValue];
    
        [self setCallback:command.callbackId];
    
    if (channel != 0) {
        [self executeCallbackWithString:[NSString stringWithFormat:@"Launching Channel %@",channelString]];
    } else {
        [self executeCallbackWithString:@"Error invalid args"];
    }
    
    
    [self setArview:[[ARViewController alloc]initWithNibName:@"ARViewController" bundle:nil]];
    
    self.arview.channel = channel;
    [self.arview setDelegate:self];

    
    
    [self.viewController presentViewController:self.arview animated:true completion:nil];
    
}

-(void)executeJSFunction:(NSString*)function withArguments:(NSArray*)args callBack:(int)callbackId webView:(UIWebView*) webview
{
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:args options:NSJSONWritingPrettyPrinted error:nil];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSLog(@"JSON Output: %@", jsonString);
    
    NSString* javascriptCall = [NSString stringWithFormat:@"%@(%@)", function,jsonString];
    
    NSString* resultString = [self.webView stringByEvaluatingJavaScriptFromString:javascriptCall];
    
    NSArray* result = [NSArray arrayWithObject:resultString];
    
//    [self returnResultTo:webview results:result withArgs:nil];
    [self returnResultTo:webview results:callbackId  withArgs:result];
    
}


-(void)executeCordovaMethod:(NSString*)function withArguments:(NSArray*)args callBack:(int)callbackId webView:(UIWebView*) webview
{
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:args options:NSJSONWritingPrettyPrinted error:nil];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSLog(@"JSON Output: %@", jsonString);
    
    NSString* javascriptCall = [NSString stringWithFormat:@"navigator.metaio.executeMethod(\"%@\",%@)", function,jsonString];
    
    NSString* resultString = [self.webView stringByEvaluatingJavaScriptFromString:javascriptCall];
    
    NSArray* result = [NSArray arrayWithObject:resultString];
    
    //    [self returnResultTo:webview results:result withArgs:nil];
    [self returnResultTo:webview results:callbackId  withArgs:result];
    
}

-(void)executeCallbackWithString:(NSString*)string
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:string];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callback];
}

- (void)returnResultTo:(UIWebView*)webview results:(int)callbackId withArgs:(NSArray*)args;
{
    NSString *resultString;
    
    NSError *writeError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:args options:NSJSONWritingPrettyPrinted error:&writeError];
    resultString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSLog(@"JSON Output: %@", resultString);
   
    [webview stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"NativeBridge.resultForCallback(%d,%@);",callbackId,resultString]];
}


// Implements all you native function in this one, by matching 'functionName' and parsing 'args'
// Use 'callbackId' with 'returnResult' selector when you get some results to send back to javascript
- (void)handleCall:(NSString*)functionName callbackId:(int)callbackId args:(NSArray*)args closeAR:(BOOL)close webView:(UIWebView *)webview
{
    
    NSLog(@"%@",functionName);
    NSLog(@"%d",callbackId);
    NSLog(@"%@",args);
    
           
    if ([functionName isEqualToString:@"getCordovaMethods"]) {
        
        [self returnResultTo:webview results:callbackId withArgs:javascriptSelectors];
        
    } else if([javascriptSelectors containsObject:functionName]){
        
        [self executeCordovaMethod:functionName withArguments:args callBack:callbackId webView:webview];
        
        if (close) {
            [self closeAR];
        }
        
    } else {
    
      [self executeJSFunction:functionName withArguments:args callBack:callbackId webView:webview];
        
        if (close) {
            [self closeAR];
        }
    
    }
    

}



-(void)closeAR
{
    [self.arview dismissViewControllerAnimated:true completion:nil];
}

@end