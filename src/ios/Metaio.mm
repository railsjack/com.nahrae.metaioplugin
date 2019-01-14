//
//  ViewController.h
//  metaio fundamentals
//
//  Created by Simone on 19/11/13.
//  Copyright (c) 2013 Simone. All rights reserved.
//

/********* Echo.h Cordova Plugin Header *******/

#import <Cordova/CDV.h>

@interface Metaio : CDVPlugin

- (void)metaio:(CDVInvokedUrlCommand*)command;

@end

/********* Echo.m Cordova Plugin Implementation *******/


#import <Cordova/CDV.h>
#import "MetaioViewController.h"

@implementation Metaio

- (void)metaio:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];
    
    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
   
    MetaioViewController *metaioViewController= [[MetaioViewController alloc]initWithNibName:@"MetaioViewController" bundle:nil];
    
    [self.viewController presentModalViewController:metaioViewController animated:true];
    
}

@end