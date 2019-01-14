/********* MetaioCloud.h Cordova Plugin Header *******/

#import <Cordova/CDV.h>
#import "ARViewController.h"
#import "ARViewDelegate.h"



@interface MetaioCloud : CDVPlugin <ARViewDelegate>

@property(nonatomic) NSString* callback;
@property(nonatomic) NSArray* javascriptSelectors;
@property(nonatomic) ARViewController* arview;


- (void)metaiocloud:(CDVInvokedUrlCommand*)command;


@end