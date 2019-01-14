//
//  ViewController.m
//  metaio fundamentals
//
//  Created by Simone on 19/11/13.
//  Copyright (c) 2013 Simone. All rights reserved.
//

#import "MetaioViewController.h"

@interface MetaioViewController ()

@end

@implementation MetaioViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    
    // load our tracking configuration
    NSString* trackingDataFile = [[NSBundle mainBundle] pathForResource:@"TrackingData_MarkerlessFast" ofType:@"xml" inDirectory:@"Assets"];
    
	if(trackingDataFile)
	{
		bool success = m_metaioSDK->setTrackingConfiguration([trackingDataFile UTF8String]);
		if( !success)
        NSLog(@"No success loading the tracking configuration");
	} else {
        NSLog(@"No success loading the tracking configuration");
    }
    
    
    // load content
    NSString* metaioManModel = [[NSBundle mainBundle] pathForResource:@"metaioman" ofType:@"md2" inDirectory:@"Assets"];
    
	if(metaioManModel)
	{
		// if this call was successful, theLoadedModel will contain a pointer to the 3D model
        metaio::IGeometry* theLoadedModel =  m_metaioSDK->createGeometry([metaioManModel UTF8String]);
        if( theLoadedModel )
        {
            // scale it a bit up
            theLoadedModel->setScale(metaio::Vector3d(4.0,4.0,4.0));
        }
        else
        {
            NSLog(@"error, could not load %@", metaioManModel);
        }
    } else {
        NSLog(@"error, could not load metaio model!");
    }
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
