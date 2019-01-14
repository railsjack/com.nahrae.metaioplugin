//
//  ASImageSharingViewController.h
//
//  Created by Stefan Misslinger on 4/6/11.
// Copyright 2007-2013 metaio GmbH. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ASImageSharingViewController : UIViewController 
{
    UIImage*    imageToPost;
    UIImageView *imageView;
}
@property (nonatomic, retain) UIImage*  imageToPost;
@property (nonatomic, retain) NSString* channelName;
@property (nonatomic, retain) IBOutlet UIImageView *imageView;

@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (nonatomic, retain) IBOutlet UIButton *btnBack;
@property (retain, nonatomic) IBOutlet UIButton *btnScreenshot;
@property (retain, nonatomic) IBOutlet UIButton *btnFacebook;
@property (retain, nonatomic) IBOutlet UIButton *btnTwitter;
@property (retain, nonatomic) IBOutletCollection(UIImageView) NSArray *buttonIcons;

- (IBAction)onButtonBackPushed:(id)sender;
- (IBAction)onButtonScreenshotPushed:(id)sender;
- (IBAction)onButtonFacebookPushed:(id)sender;
- (IBAction)onButtonTweetPushed:(id)sender;

@end
