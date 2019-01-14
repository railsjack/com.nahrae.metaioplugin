//
//  ARViewController.h
//
//  Created by Stefan Misslinger on 3/1/11.
// Copyright 2007-2013 metaio GmbH. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ARViewDelegate.h"
#import <metaioSDK/MetaioPlugin/MetaioPluginViewController.h>

/** This class represents the AR view. Implement the JunaioPluginDelegate protocol to define the channel ID to use */
@interface ARViewController : MetaioPluginViewController
{
	bool	m_useLocationAtStartup;	//!< Flag to indicate if we should use the location at startup
    
}

- (IBAction)onBtnClosePushed:(id)sender;
@property(nonatomic) int channel;
@property(nonatomic) id<ARViewDelegate> delegate;
@end