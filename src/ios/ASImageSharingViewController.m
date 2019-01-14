//
//  ASImageSharingViewController.m
//
//  Created by Stefan Misslinger on 4/6/11.
//  Copyright 2007-2013 metaio GmbH. All rights reserved.
//

#import "ASImageSharingViewController.h"
#import <Twitter/Twitter.h>
#import <Social/Social.h>
#import <QuartzCore/QuartzCore.h>

#define SYSTEM_VERSION_LESS_THAN(v)                 ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)

@implementation ASImageSharingViewController
@synthesize imageToPost;
@synthesize imageView;
@synthesize channelName;
@synthesize activityIndicator;
@synthesize btnBack;
@synthesize btnScreenshot;
@synthesize btnFacebook;
@synthesize btnTwitter;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)dealloc
{
    [imageView release];
    [activityIndicator release];
    [btnBack release];

	[btnScreenshot release];
	[btnFacebook release];
	[btnTwitter release];
	[_buttonIcons release];
    [super dealloc];
}


- (void) showAlert: (NSString*) message
{
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"" message:message delegate:self cancelButtonTitle:NSLocalizedString(@"BTN_OK", @"") otherButtonTitles:nil, nil];
    [alert show];
    [alert release];
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];

	// Set back button label
	[btnBack setTitle:NSLocalizedString(@"BTN_CLOSE", "Closeing button label") forState:UIControlStateNormal];
    imageView.image = imageToPost;
    
	// only enable twitter and facebook sharing on ios6 and higher
	if (SYSTEM_VERSION_LESS_THAN(@"6.0") )
	{
		[btnTwitter setEnabled:NO];
		[btnFacebook setEnabled:NO];
	}
	else
	{
		if( ![SLComposeViewController isAvailableForServiceType:SLServiceTypeFacebook])
		{
			[btnFacebook setEnabled:NO];
		}
		
		if( ![SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter])
		{
			[btnTwitter setEnabled:NO];
		}
	}
}


- (void)viewDidUnload
{
    [self setImageView:nil];
    [self setActivityIndicator:nil];
    [self setBtnBack:nil];

	[self setBtnScreenshot:nil];
	[self setBtnFacebook:nil];
	[self setBtnTwitter:nil];
	[self setButtonIcons:nil];
    [super viewDidUnload];
}


- (IBAction)onButtonBackPushed:(id)sender {
    	
    [self dismissModalViewControllerAnimated:YES];
}


- (IBAction)onButtonScreenshotPushed:(id)sender 
{
	[activityIndicator startAnimating];
	dispatch_async(dispatch_get_global_queue(0, 0), ^(void) {
		dispatch_async(dispatch_get_main_queue(), ^(void) {
			UIImageWriteToSavedPhotosAlbum(imageToPost, self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
		});
	});
}


- (void) postImageForServiceType: (NSString *)serviceType
{
	SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:serviceType];
	
	SLComposeViewControllerCompletionHandler myBlock = ^(SLComposeViewControllerResult result){
		[activityIndicator stopAnimating];
		[controller dismissViewControllerAnimated:YES completion:Nil];
	};
	
	controller.completionHandler = myBlock;
	
	//Adding the Text to the facebook post value from iOS
	[controller setInitialText:@""];
	
	//Adding the Image to the facebook post value from iOS
	[controller addImage:self.imageToPost];
	
	[activityIndicator startAnimating];
	[self presentViewController:controller animated:YES completion:Nil];
}


- (IBAction)onButtonFacebookPushed:(id)sender
{
	// in case you want to post your image through the Facebook SDK
	// you can implement your own code in this method.
	// more information https://www.google.com/search?q=facebook+sdk+ios+post+image
	
	[self postImageForServiceType:SLServiceTypeFacebook];
}


- (IBAction)onButtonTweetPushed:(id)sender
{
	[self postImageForServiceType:SLServiceTypeTwitter];
}


- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo
{
    [activityIndicator stopAnimating];
}



@end
